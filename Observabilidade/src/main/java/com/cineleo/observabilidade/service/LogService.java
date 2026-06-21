package com.cineleo.observabilidade.service;

import com.cineleo.observabilidade.dto.LogEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@Slf4j
public class LogService {

    private static final int MAX_LOGS = 500;
    private final ConcurrentLinkedDeque<LogEventDTO> logs = new ConcurrentLinkedDeque<>();

    public void registrar(LogEventDTO logEvent) {
        logEvent.setTimestamp(LocalDateTime.now());
        logs.addFirst(logEvent);

        while (logs.size() > MAX_LOGS) {
            logs.removeLast();
        }

        log.info("[{}] [{}] {}", logEvent.getServico(), logEvent.getNivel(), logEvent.getMensagem());
    }

    public List<LogEventDTO> listarTodos() {
        return new ArrayList<>(logs);
    }

    public List<LogEventDTO> listarPorServico(String servico) {
        return logs.stream()
                .filter(l -> l.getServico().equalsIgnoreCase(servico))
                .toList();
    }

    public List<LogEventDTO> listarPorNivel(String nivel) {
        return logs.stream()
                .filter(l -> l.getNivel().equalsIgnoreCase(nivel))
                .toList();
    }
}
