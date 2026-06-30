package com.cineleo.avaliacoes.entity;

import com.cineleo.avaliacoes.enums.StatusAvaliacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "avaliacoes")
@CompoundIndex(name = "idx_avaliacao_unica", def = "{'usuarioId': 1, 'filmeId': 1, 'reservaId': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {

    @Id
    private String id;

    private String filmeId;
    private String sessaoId;
    private String reservaId;
    private String usuarioId;
    private String usuarioNome;

    private Double nota;
    private String titulo;
    private String comentario;

    @Builder.Default
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    private Integer curtidas = 0;

    @Builder.Default
    private List<String> curtidores = new ArrayList<>();

    @Builder.Default
    private Integer denuncias = 0;

    @Builder.Default
    private StatusAvaliacao status = StatusAvaliacao.APROVADA;

    @Builder.Default
    private LocalDateTime criadaEm = LocalDateTime.now();

    private LocalDateTime atualizadaEm;
}
