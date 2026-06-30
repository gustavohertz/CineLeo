package com.cineleo.avaliacoes.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.producer.avaliacao-criada}")
    private String topicAvaliacaoCriada;

    @Value("${kafka.topics.producer.avaliacao-removida}")
    private String topicAvaliacaoRemovida;

    @Bean
    public NewTopic topicAvaliacaoCriada() {
        return TopicBuilder.name(topicAvaliacaoCriada)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic topicAvaliacaoRemovida() {
        return TopicBuilder.name(topicAvaliacaoRemovida)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
