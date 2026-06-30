package com.cineleo.avaliacoes.kafka;

import com.cineleo.avaliacoes.dto.PagamentoAprovadoEvent;
import com.cineleo.avaliacoes.service.AvaliacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PagamentoConsumer {

    private final AvaliacaoService avaliacaoService;

    @KafkaListener(
        topics = "${kafka.topics.consumer.pagamento-aprovado}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumirPagamentoAprovado(PagamentoAprovadoEvent evento) {
        log.info("[Kafka] Pagamento aprovado recebido: reservaId={}, usuarioId={}, filmeId={}",
                evento.getReservaId(), evento.getUsuarioId(), evento.getFilmeId());

        avaliacaoService.registrarElegibilidade(evento);
    }
}
