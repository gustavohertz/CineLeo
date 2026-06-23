package com.cineleo.eventos.interceptor;

public class LogEventDTO {

    private String servico;
    private String nivel;
    private String mensagem;

    public LogEventDTO() {
    }

    public LogEventDTO(String servico, String nivel, String mensagem) {
        this.servico = servico;
        this.nivel = nivel;
        this.mensagem = mensagem;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}