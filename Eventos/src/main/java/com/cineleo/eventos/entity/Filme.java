package com.cineleo.eventos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "filmes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClassificacaoIndicativa classificacaoIndicativa;

    @Embedded
    private Endereco endereco;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusFilme status = StatusFilme.ATIVO;

    @OneToMany(mappedBy = "filme", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Sessao> sessoes = new ArrayList<>();

    public enum ClassificacaoIndicativa {
        LIVRE,
        DOZE_ANOS,
        QUATORZE_ANOS,
        DEZESSEIS_ANOS,
        DEZOITO_ANOS
    }

    public enum StatusFilme {
        ATIVO, INATIVO, EM_BREVE
    }
}
