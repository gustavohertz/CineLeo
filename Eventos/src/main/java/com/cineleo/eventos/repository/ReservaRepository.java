package com.cineleo.eventos.repository;

import com.cineleo.eventos.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findBySessaoId(Long sessaoId);

    List<Reserva> findByEmailCliente(String emailCliente);

    List<Reserva> findByCpfCliente(String cpfCliente);

    Optional<Reserva> findByCodigoConfirmacao(String codigoConfirmacao);

    List<Reserva> findByStatus(Reserva.StatusReserva status);
}
