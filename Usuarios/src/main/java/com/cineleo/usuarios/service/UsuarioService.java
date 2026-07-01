package com.cineleo.usuarios.service;

import com.cineleo.usuarios.client.PagamentoClient;
import com.cineleo.usuarios.dto.UsuarioRequestDTO;
import com.cineleo.usuarios.dto.UsuarioResponseDTO;
import com.cineleo.usuarios.entity.UsuarioEntity;
import com.cineleo.usuarios.exception.ConflictException;
import com.cineleo.usuarios.exception.ResourceNotFoundException;
import com.cineleo.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PagamentoClient pagamentoClient;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        return UsuarioResponseDTO.from(usuario);
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Já existe um usuário cadastrado com o e-mail: " + dto.getEmail());
        }
        if (usuarioRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("Já existe um usuário cadastrado com o CPF: " + dto.getCpf());
        }

        // Best-effort: o cadastro NÃO depende do gateway de pagamento. Se o
        // Pagamento estiver indisponível, o usuário é criado sem customerId
        // (que pode ser preenchido depois, no primeiro pagamento).
        String customerId = null;
        try {
            customerId = pagamentoClient.criarCustomer(dto.getNome(), dto.getEmail(), dto.getCpf());
        } catch (Exception e) {
            log.warn("Cadastro seguindo sem customer de pagamento (serviço indisponível): {}", e.getMessage());
        }

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nome(dto.getNome())
                .idade(dto.getIdade())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .senhaHash(passwordEncoder.encode(dto.getSenha()))
                .ativo(true)
                .roles(Set.of("USER"))
                .customerId(customerId)   // NOVO
                .build();

        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }
}