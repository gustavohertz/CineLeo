package com.cineleo.recomendacao.service;

import com.cineleo.recomendacao.dto.RecomendacaoDTO;
import com.cineleo.recomendacao.repository.AvaliacaoRecebidaRepository;
import com.cineleo.recomendacao.repository.AvaliacaoRecebidaRepository.FilmeRanking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecomendacaoServiceTest {

    @Mock
    private AvaliacaoRecebidaRepository repository;

    @InjectMocks
    private RecomendacaoService service;

    private FilmeRanking ranking(String filmeId, double media, long total) {
        FilmeRanking r = mock(FilmeRanking.class);
        when(r.getFilmeId()).thenReturn(filmeId);
        when(r.getMedia()).thenReturn(media);
        when(r.getTotal()).thenReturn(total);
        return r;
    }

    @Test
    void topFilmes_mapeiaEArredondaMediaParaDuasCasas() {
        FilmeRanking f1 = ranking("F1", 8.333333, 3);
        FilmeRanking f2 = ranking("F2", 6.5, 2);
        when(repository.ranking(eq(1L), any(Pageable.class))).thenReturn(List.of(f1, f2));

        List<RecomendacaoDTO> result = service.topFilmes(10, 1);

        assertThat(result).containsExactly(
                new RecomendacaoDTO("F1", 8.33, 3L),
                new RecomendacaoDTO("F2", 6.5, 2L));
    }

    @Test
    void topFilmes_repassaLimiteEMinimoParaORepositorio() {
        when(repository.ranking(eq(5L), any(Pageable.class))).thenReturn(List.of());

        service.topFilmes(3, 5);

        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).ranking(eq(5L), pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(3);
    }

    @Test
    void paraUsuario_usaConsultaComExclusaoDoUsuario() {
        FilmeRanking f3 = ranking("F3", 10.0, 1);
        when(repository.rankingParaUsuario(eq("u1"), eq(1L), any(Pageable.class)))
                .thenReturn(List.of(f3));

        List<RecomendacaoDTO> result = service.paraUsuario("u1", 10, 1);

        assertThat(result).extracting(RecomendacaoDTO::filmeId).containsExactly("F3");
    }
}
