package com.cineleo.eventos.service;

import com.cineleo.eventos.dto.ReservaRequestDTO;
import com.cineleo.eventos.dto.ReservaResponseDTO;
import com.cineleo.eventos.entity.Reserva;
import com.cineleo.eventos.entity.Sessao;
import com.cineleo.eventos.exception.BusinessException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.cineleo.eventos.repository.ReservaRepository;
import com.cineleo.eventos.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SessaoRepository sessaoRepository;

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
                .nomeCliente(dto.getNomeCliente())
                .emailCliente(dto.getEmailCliente())
                .cpfCliente(dto.getCpfCliente())
                .quantidadeIngressos(dto.getQuantidadeIngressos())
                .valorTotal(valorTotal)
                .codigoConfirmacao(gerarCodigoConfirmacao())
                .build();

        return ReservaResponseDTO.from(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO confirmar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com id: " + id));

        if (reserva.getStatus() != Reserva.StatusReserva.PENDENTE) {
            throw new BusinessException("Somente reservas com status PENDENTE podem ser confirmadas");
        }

        reserva.setStatus(Reserva.StatusReserva.CONFIRMADA);
        return ReservaResponseDTO.from(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com id: " + id));

        if (reserva.getStatus() == Reserva.StatusReserva.CANCELADA) {
            throw new BusinessException("Reserva já está cancelada");
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
