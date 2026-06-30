package com.cineleo.avaliacoes.kafka;

import com.cineleo.avaliacoes.dto.AvaliacaoCriadaEvent;
import com.cineleo.avaliacoes.dto.AvaliacaoRemovidaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AvaliacaoProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.producer.avaliacao-criada}")
    private String topicAvaliacaoCriada;

    @Value("${kafka.topics.producer.avaliacao-removida}")
    private String topicAvaliacaoRemovida;

    public void publicarAvaliacaoCriada(AvaliacaoCriadaEvent evento) {
        log.info("[Kafka] Publicando avaliacao.criada: filmeId={}, usuarioId={}",
                evento.getFilmeId(), evento.getUsuarioId());
        kafkaTemplate.send(topicAvaliacaoCriada, evento.getFilmeId(), evento);
    }

    public void publicarAvaliacaoRemovida(AvaliacaoRemovidaEvent evento) {
        log.info("[Kafka] Publicando avaliacao.removida: avaliacaoId={}", evento.getAvaliacaoId());
        kafkaTemplate.send(topicAvaliacaoRemovida, evento.getFilmeId(), evento);
    }
}
