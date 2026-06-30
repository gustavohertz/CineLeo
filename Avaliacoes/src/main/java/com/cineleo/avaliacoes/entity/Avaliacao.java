package com.cineleo.avaliacoes.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "avaliacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Id
    private String id;

    private String filmeId;
    private String usuarioId;
    private String usuarioNome;
    private Double nota;
    private String comentario;

    @Builder.Default
    private LocalDateTime criadaEm = LocalDateTime.now();

    private LocalDateTime atualizadaEm;
}
