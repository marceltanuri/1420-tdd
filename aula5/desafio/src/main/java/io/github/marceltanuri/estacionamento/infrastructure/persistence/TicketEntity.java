package io.github.marceltanuri.estacionamento.infrastructure.persistence;

import jakarta.persistence.*;
import io.github.marceltanuri.estacionamento.infrastructure.persistence.VeiculoEntity;
import io.github.marceltanuri.estacionamento.domain.ticket.Status; // Domain Status enum

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;


@Entity
@Table(name = "tickets")
public class TicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "veiculo_placa", nullable = false)
    private VeiculoEntity veiculo;

    @Column(nullable = false)
    private LocalDateTime entrada;

    private LocalDateTime saida;

    private LocalDateTime pagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public TicketEntity() {
    }

    public TicketEntity(VeiculoEntity veiculo, LocalDateTime entrada, Status status) {
        this.veiculo = veiculo;
        this.entrada = entrada;
        this.status = status;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VeiculoEntity getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(VeiculoEntity veiculo) {
        this.veiculo = veiculo;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public void setEntrada(LocalDateTime entrada) {
        this.entrada = entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public void setSaida(LocalDateTime saida) {
        this.saida = saida;
    }

    public LocalDateTime getPagamento() {
        return pagamento;
    }

    public void setPagamento(LocalDateTime pagamento) {
        this.pagamento = pagamento;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
