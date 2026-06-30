package com.cineleo.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Destino dos {@code fallbackUri} das rotas. Quando o circuit breaker de um
 * serviço abre (ou a chamada estoura o timeout), o Gateway encaminha a
 * requisição para cá em vez de devolver um 500 cru — degradação graciosa.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/{servico}")
    public ResponseEntity<Map<String, Object>> fallback(@PathVariable String servico) {
        Map<String, Object> body = Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", 503,
                "error", "Service Unavailable",
                "servico", servico,
                "mensagem", "O serviço '" + servico + "' está temporariamente indisponível. Tente novamente em instantes."
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }
}
