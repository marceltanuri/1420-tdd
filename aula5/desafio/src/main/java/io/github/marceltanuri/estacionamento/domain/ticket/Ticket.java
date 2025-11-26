package io.github.marceltanuri.estacionamento.domain.ticket;

import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {

    private String id;
    private Veiculo veiculo;
    private LocalDateTime entrada;
    private LocalDateTime saida;
    private LocalDateTime pagamento;
    private Status status;

    private Ticket() {
    }

    public static Ticket novo(Veiculo veiculo, LocalDateTime entrada) {
        Ticket ticket = new Ticket();
        ticket.veiculo = veiculo;
        ticket.entrada = entrada;
        ticket.status = Status.PENDENTE;
        return ticket;
    }

    public void pagar(LocalDateTime pagamento) {
        if (status != Status.PENDENTE) {
            throw new IllegalStateException("Ticket com status " + status + " não pode ser pago.");
        }
        this.pagamento = pagamento;
        this.status = Status.PAGO;
    }

    public void expirarPagamento(){
        if (status != Status.PAGO) {
            throw new IllegalStateException("Ticket com status " + status + " não pode ser expirado.");
        }
        this.status = Status.TOLERANCIA_APOS_PAGAMENTO_EXPIRADO;
    }

    public void expirarIsencao(){
        if (status != Status.ISENTO) {
            throw new IllegalStateException("Ticket com status " + status + " não pode ser expirado.");
        }
        this.status = Status.TOLERANCIA_APOS_ISENCAO_EXPIRADO;
    }

    public void finalizar(LocalDateTime saida) {
        if (status == Status.FINALIZADO) {
            throw new IllegalStateException("Ticket já finalizado.");
        }
        this.saida = saida;
        this.status = Status.FINALIZADO;
    }

    public void isentar(Status status) {
        if (status != Status.ISENTO && status != Status.ISENTO_FUNCIONARIO) {
            throw new IllegalArgumentException("Status de isenção inválido");
        }
        if (this.status == Status.FINALIZADO) {
            throw new IllegalStateException("Ticket no status " + this.status + " não pode ser isentado.");
        }
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public LocalDateTime getEntrada() {
        return entrada;
    }

    public LocalDateTime getSaida() {
        return saida;
    }

    public LocalDateTime getPagamento() {
        return pagamento;
    }

    public Status getStatus() {
        return status;
    }

    private void setStatus(Status status) {
        this.status = status;
    }
}
