package com.cineleo.recomendacao.consumer;

import com.cineleo.recomendacao.dto.AvaliacaoCriadaEvent;
import com.cineleo.recomendacao.entity.AvaliacaoRecebida;
import com.cineleo.recomendacao.repository.AvaliacaoRecebidaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AvaliacaoEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(AvaliacaoEventConsumer.class);

    private final AvaliacaoRecebidaRepository repository;

    public AvaliacaoEventConsumer(AvaliacaoRecebidaRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "${cineleo.topics.avaliacao-criada}", groupId = "recomendacao-group")
    public void onAvaliacaoCriada(AvaliacaoCriadaEvent evento) {
        if (evento.avaliacaoId() == null || evento.filmeId() == null || evento.nota() == null) {
            log.warn("Evento de avaliação ignorado (campos obrigatórios ausentes): {}", evento);
            return;
        }
        repository.save(new AvaliacaoRecebida(
                evento.avaliacaoId(), evento.filmeId(), evento.usuarioId(), evento.nota()));
        log.info("Avaliação registrada para ranking: filme={}, nota={}", evento.filmeId(), evento.nota());
    }
}
