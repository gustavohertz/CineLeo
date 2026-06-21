package com.infnet.microservicesKafka.consumer;

import com.infnet.microservicesKafka.client.NotificationClient;
import com.infnet.microservicesKafka.dto.PagamentoEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class PagamentoEventoConsumer {

    private static final Logger log = LoggerFactory.getLogger(PagamentoEventoConsumer.class);

    private final NotificationClient notificationClient;

    public PagamentoEventoConsumer(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    @KafkaListener(topics = "cinema.pagamento.aprovado", groupId = "cineleo-notificacoes")
    public void handlePagamentoAprovado(PagamentoEvento evento) {
        log.info("Recebido evento de pagamento APROVADO: reserva={}, email={}", evento.idReserva(), evento.emailUsuario());
        String mensagem = String.format(
                "Pagamento aprovado! Reserva %s, valor do pagamento: %s",
                evento.idReserva(),
                evento.idPagamento()
        );
        notificationClient.createAndSendEmail(
                evento.idUsuario(),
                evento.emailUsuario(),
                mensagem,
                OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }

    @KafkaListener(topics = "cinema.pagamento.recusado", groupId = "cineleo-notificacoes")
    public void handlePagamentoRecusado(PagamentoEvento evento) {
        log.info("Recebido evento de pagamento RECUSADO: reserva={}, email={}", evento.idReserva(), evento.emailUsuario());
        String mensagem = String.format(
                "Pagamento recusado para reserva %s. Motivo: %s",
                evento.idReserva(),
                evento.mensagemErro() != null ? evento.mensagemErro() : "não informado"
        );
        notificationClient.createAndSendEmail(
                evento.idUsuario(),
                evento.emailUsuario(),
                mensagem,
                OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }
}