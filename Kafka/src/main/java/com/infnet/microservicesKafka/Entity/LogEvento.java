package com.infnet.microservicesKafka.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class LogEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String topico;
    private String emailUsuario;
    private String status;
    private String mensagemErro;
    private LocalDateTime dataHora;

    public LogEvento(String topico, String emailUsuario, String status, String mensagemErro) {
        this.topico = topico;
        this.emailUsuario = emailUsuario;
        this.status = status;
        this.mensagemErro = mensagemErro;
        this.dataHora = LocalDateTime.now();
    }
}
