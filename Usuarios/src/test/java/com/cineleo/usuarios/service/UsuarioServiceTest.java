package com.cineleo.usuarios.service;

import com.cineleo.usuarios.dto.UsuarioRequestDTO;
import com.cineleo.usuarios.dto.UsuarioResponseDTO;
import com.cineleo.usuarios.entity.UsuarioEntity;
import com.cineleo.usuarios.exception.ConflictException;
import com.cineleo.usuarios.exception.ResourceNotFoundException;
import com.cineleo.usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private UsuarioEntity criarUsuario() {
        return UsuarioEntity.builder()
                .id(1L)
                .nome("João Silva")
                .idade(25)
                .email("joao@email.com")
                .cpf("12345678901")
                .senhaHash("hash123")
                .ativo(true)
                .criadoEm(LocalDateTime.now())
                .roles(Set.of("USER"))
                .build();
    }

    @Test
    void deveListarTodosOsUsuarios() {
        UsuarioEntity usuario = criarUsuario();
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> resultado = usuarioService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        UsuarioEntity usuario = criarUsuario();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO resultado = usuarioService.buscarPorId(1L);

        assertEquals("João Silva", resultado.getNome());
        assertEquals("joao@email.com", resultado.getEmail());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.buscarPorId(99L));
    }

    @Test
    void deveCriarUsuarioComSucesso() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setNome("Maria");
        dto.setIdade(30);
        dto.setEmail("maria@email.com");
        dto.setCpf("98765432100");
        dto.setSenha("senha123");

        when(usuarioRepository.existsByEmail("maria@email.com")).thenReturn(false);
        when(usuarioRepository.existsByCpf("98765432100")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hash_encoded");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> {
            UsuarioEntity u = invocation.getArgument(0);
            u.setId(2L);
            u.setCriadoEm(LocalDateTime.now());
            return u;
        });

        UsuarioResponseDTO resultado = usuarioService.criar(dto);

        assertEquals("Maria", resultado.getNome());
        verify(passwordEncoder).encode("senha123");
    }

    @Test
    void deveLancarExcecaoQuandoEmailDuplicado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setEmail("joao@email.com");
        dto.setCpf("12345678901");

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> usuarioService.criar(dto));
    }

    @Test
    void deveLancarExcecaoQuandoCpfDuplicado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO();
        dto.setEmail("novo@email.com");
        dto.setCpf("12345678901");

        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(false);
        when(usuarioRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThrows(ConflictException.class, () -> usuarioService.criar(dto));
    }
}
