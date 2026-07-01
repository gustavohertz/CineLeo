package com.cineleo.recomendacao.consumer;

import com.cineleo.recomendacao.dto.AvaliacaoCriadaEvent;
import com.cineleo.recomendacao.entity.AvaliacaoRecebida;
import com.cineleo.recomendacao.repository.AvaliacaoRecebidaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvaliacaoEventConsumerTest {

    @Mock
    private AvaliacaoRecebidaRepository repository;

    @InjectMocks
    private AvaliacaoEventConsumer consumer;

    @Test
    void eventoValido_persisteAvaliacaoParaORanking() {
        consumer.onAvaliacaoCriada(new AvaliacaoCriadaEvent("a1", "F1", "u1", 9.0));

        ArgumentCaptor<AvaliacaoRecebida> captor = ArgumentCaptor.forClass(AvaliacaoRecebida.class);
        verify(repository).save(captor.capture());

        AvaliacaoRecebida salva = captor.getValue();
        assertThat(salva.getAvaliacaoId()).isEqualTo("a1");
        assertThat(salva.getFilmeId()).isEqualTo("F1");
        assertThat(salva.getUsuarioId()).isEqualTo("u1");
        assertThat(salva.getNota()).isEqualTo(9.0);
    }

    @Test
    void eventoSemNota_eIgnorado() {
        consumer.onAvaliacaoCriada(new AvaliacaoCriadaEvent("a1", "F1", "u1", null));
        verify(repository, never()).save(any());
    }

    @Test
    void eventoSemFilme_eIgnorado() {
        consumer.onAvaliacaoCriada(new AvaliacaoCriadaEvent("a1", null, "u1", 9.0));
        verify(repository, never()).save(any());
    }

    @Test
    void eventoRemocao_apagaAvaliacaoExistenteDoRanking() {
        when(repository.existsById("a1")).thenReturn(true);

        consumer.onAvaliacaoRemovida(new AvaliacaoCriadaEvent("a1", null, null, null));

        verify(repository).deleteById("a1");
    }

    @Test
    void eventoRemocao_semAvaliacaoIdNaoApagaNada() {
        consumer.onAvaliacaoRemovida(new AvaliacaoCriadaEvent(null, null, null, null));
        verify(repository, never()).deleteById(any());
    }
}
