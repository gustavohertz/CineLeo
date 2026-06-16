package com.cineleo.eventos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @Column(nullable = false)
    private Integer capacidade;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoSala tipo;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Sessao> sessoes = new ArrayList<>();

    public enum TipoSala {
        STANDARD, IMAX, DOLBY, VIP, DRIVE_IN
    }
}
