package com.cineleo.eventos.service;

import com.cineleo.eventos.client.PagamentoClient;
import com.cineleo.eventos.client.UsuarioClient;
import com.cineleo.eventos.dto.PagamentoReservaRequestDTO;
import com.cineleo.eventos.dto.ReservaRequestDTO;
import com.cineleo.eventos.dto.ReservaResponseDTO;
import com.cineleo.eventos.dto.PagamentoEvento;
import com.cineleo.eventos.entity.Reserva;
import com.cineleo.eventos.entity.Sessao;
import com.cineleo.eventos.exception.BusinessException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.cineleo.eventos.repository.ReservaRepository;
import com.cineleo.eventos.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SessaoRepository sessaoRepository;
    private final UsuarioClient usuarioClient;
    private final PagamentoClient pagamentoClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorSessao(Long sessaoId) {
        return reservaRepository.findBySessaoId(sessaoId).stream()
                .map(ReservaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorEmail(String email) {
        return reservaRepository.findByEmailCliente(email).stream()
                .map(ReservaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarPorId(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com id: " + id));
        return ReservaResponseDTO.from(reserva);
    }

    @Transactional(readOnly = true)
    public ReservaResponseDTO buscarPorCodigo(String codigo) {
        Reserva reserva = reservaRepository.findByCodigoConfirmacao(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com código: " + codigo));
        return ReservaResponseDTO.from(reserva);
    }

    @Transactional
    public ReservaResponseDTO criar(ReservaRequestDTO dto) {
        UsuarioClient.UsuarioDTO usuario = usuarioClient.buscarPorId(dto.getUsuarioId());
        if ("INATIVO".equals(usuario.getStatus())) {
            throw new BusinessException("Usuário inativo não pode realizar reservas");
        }

        Sessao sessao = sessaoRepository.findById(dto.getSessaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada com id: " + dto.getSessaoId()));

        if (sessao.getStatus() != Sessao.StatusSessao.AGENDADA) {
            throw new BusinessException("Reservas só podem ser feitas para sessões com status AGENDADA");
        }
        if (sessao.getAssentosDisponiveis() < dto.getQuantidadeIngressos()) {
            throw new BusinessException("Assentos insuficientes. Disponíveis: " + sessao.getAssentosDisponiveis());
        }

        BigDecimal valorTotal = sessao.getPreco().multiply(BigDecimal.valueOf(dto.getQuantidadeIngressos()));

        sessao.setAssentosDisponiveis(sessao.getAssentosDisponiveis() - dto.getQuantidadeIngressos());
        sessaoRepository.save(sessao);

        Reserva reserva = Reserva.builder()
                .sessao(sessao)
                .usuarioId(dto.getUsuarioId())
                .nomeCliente(usuario.getNome())
                .emailCliente(usuario.getEmail())
                .cpfCliente(usuario.getCpf())
                .quantidadeIngressos(dto.getQuantidadeIngressos())
                .valorTotal(valorTotal)
                .codigoConfirmacao(gerarCodigoConfirmacao())
                .build();

        return ReservaResponseDTO.from(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO pagar(Long reservaId, PagamentoReservaRequestDTO dto) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com id: " + reservaId));

        if (reserva.getStatus() != Reserva.StatusReserva.PENDENTE) {
            throw new BusinessException("Somente reservas PENDENTES podem ser pagas");
        }

        String customerId = pagamentoClient.criarCustomer(
                reserva.getNomeCliente(),
                reserva.getEmailCliente(),
                reserva.getCpfCliente()
        );

        PagamentoClient.CartaoDTO cartao = new PagamentoClient.CartaoDTO();
        cartao.setNumero(dto.getCartao().getNumero());
        cartao.setNomeTitular(dto.getCartao().getNomeTitular());
        cartao.setMesExpiracao(dto.getCartao().getMesExpiracao());
        cartao.setAnoExpiracao(dto.getCartao().getAnoExpiracao());
        cartao.setCvv(dto.getCartao().getCvv());

        String descricao = "Ingresso: " + reserva.getSessao().getFilme().getNome()
                + " - " + reserva.getQuantidadeIngressos() + "x";

        String pagamentoId = pagamentoClient.processarPagamento(
                customerId,
                reserva.getValorTotal().doubleValue(),
                descricao,
                cartao
        );

        boolean aprovado = pagamentoClient.verificarPagamento(pagamentoId);

        reserva.setIdPagamentoExterno(pagamentoId);

        if (aprovado) {
            reserva.setStatus(Reserva.StatusReserva.CONFIRMADA);
            PagamentoEvento evento = new PagamentoEvento(
                reserva.getId().toString(), reserva.getUsuarioId().toString(), reserva.getEmailCliente(), reserva.getCpfCliente(), 
                "APROVADO", null, pagamentoId
            );
            kafkaTemplate.send("cinema.pagamento.aprovado", evento);
        } else {
            reserva.setStatus(Reserva.StatusReserva.PAGAMENTO_RECUSADO);
            Sessao sessao = reserva.getSessao();
            sessao.setAssentosDisponiveis(sessao.getAssentosDisponiveis() + reserva.getQuantidadeIngressos());
            sessaoRepository.save(sessao);
            PagamentoEvento evento = new PagamentoEvento(
                reserva.getId().toString(), reserva.getUsuarioId().toString(), reserva.getEmailCliente(), reserva.getCpfCliente(), 
                "RECUSADO", "O cartão foi recusado pelo banco.", pagamentoId
            );
            kafkaTemplate.send("cinema.pagamento.recusado", evento);
        }

        return ReservaResponseDTO.from(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com id: " + id));

        if (reserva.getStatus() == Reserva.StatusReserva.CANCELADA) {
            throw new BusinessException("Reserva já está cancelada");
        }
        if (reserva.getStatus() == Reserva.StatusReserva.CONFIRMADA) {
            throw new BusinessException("Reservas confirmadas não podem ser canceladas diretamente. Entre em contato com o suporte.");
        }

        Sessao sessao = reserva.getSessao();
        sessao.setAssentosDisponiveis(sessao.getAssentosDisponiveis() + reserva.getQuantidadeIngressos());
        sessaoRepository.save(sessao);

        reserva.setStatus(Reserva.StatusReserva.CANCELADA);
        return ReservaResponseDTO.from(reservaRepository.save(reserva));
    }

    private String gerarCodigoConfirmacao() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
