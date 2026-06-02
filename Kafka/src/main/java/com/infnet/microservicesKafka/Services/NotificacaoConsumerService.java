package com.infnet.microservicesKafka.Services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.infnet.microservicesKafka.dto.PagamentoEvento;

@Component
public class NotificacaoConsumerService {

    @KafkaListener(topics = "cinema.pagamento.aprovado", groupId = "notificacaoGroup")
    public void onPagamentoAprovado(PagamentoEvento evento) {
        System.out.println("Enviando e-mail de SUCESSO para: " + evento.emailUsuario());
        System.out.println("Sua reserva " + evento.idReserva() + " está confirmada. Bom filme!");
    }

    @KafkaListener(topics = "cinema.pagamento.aprovado", groupId = "notificacaoGroup")
    public void onPagamentoRecusado(PagamentoEvento evento) {
        System.out.println("Enviando e-mail de FALHA para: " + evento.emailUsuario());
        System.out.println("Erro no pagamento da reserva " + evento.idReserva() + ": " + evento.mensagemErro());
    }
}