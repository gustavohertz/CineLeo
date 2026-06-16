package com.cineleo.usuarios.service;

import com.cineleo.usuarios.dto.UsuarioRequestDTO;
import com.cineleo.usuarios.dto.UsuarioResponseDTO;
import com.cineleo.usuarios.entity.Usuario;
import com.cineleo.usuarios.exception.ConflictException;
import com.cineleo.usuarios.exception.ResourceNotFoundException;
import com.cineleo.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarAtivos() {
        return usuarioRepository.findByStatus(Usuario.StatusUsuario.ATIVO).stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        return UsuarioResponseDTO.from(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com e-mail: " + email));
        return UsuarioResponseDTO.from(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorCpf(String cpf) {
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com CPF: " + cpf));
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

        Usuario usuario = Usuario.builder()
                .nome(dto.getNome())
                .idade(dto.getIdade())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .build();

        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));

        if (!usuario.getEmail().equals(dto.getEmail()) && usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Já existe outro usuário cadastrado com o e-mail: " + dto.getEmail());
        }
        if (!usuario.getCpf().equals(dto.getCpf()) && usuarioRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("Já existe outro usuário cadastrado com o CPF: " + dto.getCpf());
        }

        usuario.setNome(dto.getNome());
        usuario.setIdade(dto.getIdade());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());

        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    @Transactional
    public void inativar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com id: " + id));
        usuario.setStatus(Usuario.StatusUsuario.INATIVO);
        usuarioRepository.save(usuario);
    }
}
