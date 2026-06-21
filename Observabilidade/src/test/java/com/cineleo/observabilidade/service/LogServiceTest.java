package com.cineleo.observabilidade.service;

import com.cineleo.observabilidade.dto.LogEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceTest {

    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = new LogService();
    }

    @Test
    void deveRegistrarLog() {
        LogEventDTO log = new LogEventDTO();
        log.setServico("eventos-service");
        log.setNivel("INFO");
        log.setMensagem("Teste de log");

        logService.registrar(log);

        List<LogEventDTO> todos = logService.listarTodos();
        assertEquals(1, todos.size());
        assertEquals("eventos-service", todos.get(0).getServico());
        assertNotNull(todos.get(0).getTimestamp());
    }

    @Test
    void deveListarPorServico() {
        LogEventDTO log1 = new LogEventDTO();
        log1.setServico("eventos-service");
        log1.setNivel("INFO");
        log1.setMensagem("Log 1");

        LogEventDTO log2 = new LogEventDTO();
        log2.setServico("usuarios-service");
        log2.setNivel("ERROR");
        log2.setMensagem("Log 2");

        logService.registrar(log1);
        logService.registrar(log2);

        List<LogEventDTO> resultado = logService.listarPorServico("eventos-service");
        assertEquals(1, resultado.size());
        assertEquals("eventos-service", resultado.get(0).getServico());
    }

    @Test
    void deveListarPorNivel() {
        LogEventDTO log1 = new LogEventDTO();
        log1.setServico("eventos-service");
        log1.setNivel("INFO");
        log1.setMensagem("Log info");

        LogEventDTO log2 = new LogEventDTO();
        log2.setServico("usuarios-service");
        log2.setNivel("ERROR");
        log2.setMensagem("Log erro");

        logService.registrar(log1);
        logService.registrar(log2);

        List<LogEventDTO> resultado = logService.listarPorNivel("ERROR");
        assertEquals(1, resultado.size());
        assertEquals("ERROR", resultado.get(0).getNivel());
    }

    @Test
    void deveRespeitarLimiteMaximoDeLogs() {
        for (int i = 0; i < 510; i++) {
            LogEventDTO log = new LogEventDTO();
            log.setServico("servico-" + i);
            log.setNivel("INFO");
            log.setMensagem("Log " + i);
            logService.registrar(log);
        }

        List<LogEventDTO> todos = logService.listarTodos();
        assertTrue(todos.size() <= 500);
    }
}
