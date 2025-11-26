package io.github.marceltanuri.estacionamento.infrastructure;

import io.github.marceltanuri.estacionamento.domain.ticket.Status;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.TicketJpaRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;

    public TicketRepositoryImpl(TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = ticketJpaRepository;
    }

    @Override
    public Ticket save(Ticket ticket) {
        return ticketJpaRepository.save(ticket);
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return ticketJpaRepository.findById(id);
    }

    @Override
    public Optional<Ticket> findByVeiculoPlacaAndStatusNotFinalizado(String placa) {
        return ticketJpaRepository.findByVeiculoPlacaAndStatusNot(placa, Status.FINALIZADO);
    }

    @Override
    public void delete(Ticket ticket) {
        ticketJpaRepository.delete(ticket);
    }
}
