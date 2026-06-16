package com.cineleo.eventos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 100)
    private String nomeCliente;

    @Column(nullable = false, length = 150)
    private String emailCliente;

    @Column(nullable = false, length = 14)
    private String cpfCliente;

    @Column(nullable = false)
    private Integer quantidadeIngressos;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusReserva status = StatusReserva.PENDENTE;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column
    private LocalDateTime atualizadoEm;

    @Column(length = 100)
    private String idPagamentoExterno;

    @Column(length = 200)
    private String codigoConfirmacao;

    @PreUpdate
    private void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

    public enum StatusReserva {
        PENDENTE, CONFIRMADA, CANCELADA, PAGAMENTO_RECUSADO
    }
}
