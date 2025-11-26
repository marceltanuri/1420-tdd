package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;

import java.time.LocalDateTime;

import java.time.Clock;

public class EmissaoService {

    private final TicketRepository ticketRepository;
    private final Clock clock;

    public EmissaoService(TicketRepository ticketRepository, Clock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    public Ticket emitir(Veiculo veiculo) {
        return ticketRepository.findByVeiculoPlacaAndStatusNotFinalizado(veiculo.getPlaca())
                .orElseGet(() -> {
                    try {
                        Ticket novoTicket = Ticket.novo(veiculo, LocalDateTime.now(clock));
                        return ticketRepository.save(novoTicket);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao emitir o ticket.", e);
                    }
                });
    }

}