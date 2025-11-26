package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.ticket.Status;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.FuncionarioRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.ValidadorComprovante;

public class IsencaoService {

    private final TicketRepository ticketRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ValidadorComprovante validadorComprovante;

    public IsencaoService(TicketRepository ticketRepository, FuncionarioRepository funcionarioRepository, ValidadorComprovante validadorComprovante) {
        this.ticketRepository = ticketRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.validadorComprovante = validadorComprovante;
    }

    public void isentarPorComprovanteDeCompra(Ticket ticket, String comprovante) {
        if (validadorComprovante.validar(comprovante)) {
            ticket.isentar(Status.ISENTO);
            ticketRepository.save(ticket);
        } else {
            throw new IllegalArgumentException("Comprovante inválido.");
        }
    }

    public void isentarFuncionario(Ticket ticket) {
        if (funcionarioRepository.isFuncionario(ticket.getVeiculo().getPlaca())) {
            ticket.isentar(Status.ISENTO_FUNCIONARIO);
            ticketRepository.save(ticket);
        } else {
            throw new IllegalArgumentException("Placa não pertence a um funcionário.");
        }
    }

}