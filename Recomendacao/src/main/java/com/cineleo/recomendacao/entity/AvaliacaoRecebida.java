package com.cineleo.recomendacao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Cópia local da avaliação (só o que o ranking precisa), alimentada pelos
// eventos do Avaliacoes. A PK é o id da avaliação, o que torna o consumo
// idempotente: reprocessar o mesmo evento apenas sobrescreve a linha.
@Entity
@Table(name = "avaliacao_recebida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoRecebida {

    @Id
    private String avaliacaoId;

    private String filmeId;

    private String usuarioId;

    private Double nota;
}
