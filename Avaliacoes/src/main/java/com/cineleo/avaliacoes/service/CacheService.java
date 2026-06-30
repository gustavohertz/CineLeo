package com.cineleo.avaliacoes.service;

import com.cineleo.avaliacoes.dto.AvaliacaoResumoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PREFIX_RESUMO = "cineleo:avaliacoes:resumo:";
    private static final Duration TTL_RESUMO = Duration.ofMinutes(10);

    public void salvarResumo(String filmeId, AvaliacaoResumoDTO resumo) {
        try {
            String chave = PREFIX_RESUMO + filmeId;
            redisTemplate.opsForValue().set(chave, resumo, TTL_RESUMO);
            log.info("[Cache] Resumo salvo para filmeId={}", filmeId);
        } catch (Exception e) {
            log.warn("[Cache] Nao foi possivel salvar resumo no Redis para filmeId={}. Motivo: {}", filmeId, e.getMessage());
        }
    }

    public AvaliacaoResumoDTO buscarResumo(String filmeId) {
        try {
            String chave = PREFIX_RESUMO + filmeId;
            return (AvaliacaoResumoDTO) redisTemplate.opsForValue().get(chave);
        } catch (Exception e) {
            log.warn("[Cache] Nao foi possivel buscar resumo no Redis para filmeId={}. Motivo: {}", filmeId, e.getMessage());
            return null;
        }
    }

    public void invalidarResumo(String filmeId) {
        try {
            String chave = PREFIX_RESUMO + filmeId;
            redisTemplate.delete(chave);
            log.info("[Cache] Resumo invalidado para filmeId={}", filmeId);
        } catch (Exception e) {
            log.warn("[Cache] Nao foi possivel invalidar cache no Redis para filmeId={}. Motivo: {}", filmeId, e.getMessage());
        }
    }
}
