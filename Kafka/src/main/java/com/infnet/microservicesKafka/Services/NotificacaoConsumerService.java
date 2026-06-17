package com.infnet.microservicesKafka.Services;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.infnet.microservicesKafka.dto.PagamentoEvento;
import com.infnet.microservicesKafka.Entity.LogEvento;
import com.infnet.microservicesKafka.Repository.LogEventoRepository;
@Component
public class NotificacaoConsumerService {
    private final LogEventoRepository logRepository;
    public NotificacaoConsumerService(LogEventoRepository logRepository) {
        this.logRepository = logRepository;
    }
    @KafkaListener(topics = "cinema.pagamento.aprovado", groupId = "notificacaoGroup")
    public void onPagamentoAprovado(PagamentoEvento evento) {

        LogEvento log = new LogEvento(
                "cinema.pagamento.aprovado",
                evento.emailUsuario(),
                "APROVADO",
                null
        );

        logRepository.save(log);
    }
    @KafkaListener(topics = "cinema.pagamento.recusado", groupId = "notificacaoGroup")
    public void onPagamentoRecusado(PagamentoEvento evento) {
        LogEvento log = new LogEvento(
                "cinema.pagamento.recusado",
                evento.emailUsuario(),
                "RECUSADO",
                evento.mensagemErro()
        );
        logRepository.save(log);
    }
}