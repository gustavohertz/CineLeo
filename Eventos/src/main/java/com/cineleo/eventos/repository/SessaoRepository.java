package com.cineleo.eventos.repository;

import com.cineleo.eventos.entity.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    List<Sessao> findByFilmeId(Long filmeId);

    List<Sessao> findBySalaId(Long salaId);

    List<Sessao> findByStatus(Sessao.StatusSessao status);

    @Query("SELECT s FROM Sessao s WHERE s.filme.id = :filmeId AND s.status = 'AGENDADA' AND s.dataHoraInicio >= :agora")
    List<Sessao> findSessoesDisponiveisByFilme(@Param("filmeId") Long filmeId, @Param("agora") LocalDateTime agora);

    @Query("SELECT s FROM Sessao s WHERE s.sala.id = :salaId AND s.status != 'CANCELADA' AND ((s.dataHoraInicio BETWEEN :inicio AND :fim) OR (s.dataHoraFim BETWEEN :inicio AND :fim))")
    List<Sessao> findConflitosNaSala(@Param("salaId") Long salaId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
