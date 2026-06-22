package com.infnet.microservicesKafka.interceptor;

import java.time.LocalDateTime;

public class LogEventDTO {

    private String servico;
    private String nivel;
    private String mensagem;
    private LocalDateTime timestamp;

    public LogEventDTO() {}

    public LogEventDTO(String servico, String nivel, String mensagem) {
        this.servico = servico;
        this.nivel = nivel;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
    }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
