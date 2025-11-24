package io.github.marceltanuri.estacionamento.domain;

import java.time.LocalDateTime;

public class Ticket {

    private int id;
    private final Veiculo veiculo;
    private final LocalDateTime entrada;
    private LocalDateTime saida;
    private Status status;

    private Ticket(int id, Veiculo veiculo) {
        this.id = id;
        this.veiculo = veiculo;
        this.entrada = LocalDateTime.now();
        this.status = Status.PENDENTE;
    }

    public static Ticket emitir(ContadorTicket idGenerator, Veiculo veiculo) {
        return new Ticket(idGenerator.proximoId(), veiculo);
    }

    public int getId() {
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

    public Status getStatus() {
        return status;
    }


}
