package com.cineleo.eventos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5434/eventos_db",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EventosApplicationTests {

    @Test
    void contextLoads() {
    }
}
