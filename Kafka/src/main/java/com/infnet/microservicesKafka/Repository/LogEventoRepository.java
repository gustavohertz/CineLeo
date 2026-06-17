package com.infnet.microservicesKafka.Repository;

import com.infnet.microservicesKafka.Entity.LogEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEventoRepository extends JpaRepository<LogEvento, Long> {
}
