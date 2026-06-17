package com.infnet.microservicesKafka.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "tb_log_eventos")
public class LogEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String topico;
    private String emailUsuario;
    private String statusPagamento;
    private String mensagemErro;
    private LocalDateTime dataHoraRecebimento;

    public LogEvento() {}

    public LogEvento(String topico, String emailUsuario, String statusPagamento, String mensagemErro) {
        this.topico = topico;
        this.emailUsuario = emailUsuario;
        this.statusPagamento = statusPagamento;
        this.mensagemErro = mensagemErro;
        this.dataHoraRecebimento = LocalDateTime.now();
    }
}