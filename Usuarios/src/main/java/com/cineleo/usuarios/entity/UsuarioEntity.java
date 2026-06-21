package com.cineleo.usuarios.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios", schema = "auth_service")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false)
    private Integer idade;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(name = "terminal_id", length = 50)
    private String terminalId;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "usuario_roles",
            schema = "auth_service",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @PrePersist
    private void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}
