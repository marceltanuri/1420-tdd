package io.github.marceltanuri.estacionamento.infrastructure.repository;

import io.github.marceltanuri.estacionamento.domain.ticket.Status;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.infrastructure.dao.TicketJpaRepository;
import io.github.marceltanuri.estacionamento.infrastructure.persistence.TicketEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TicketRepositoryImpl implements TicketRepository {

    private final TicketJpaRepository ticketJpaRepository;
    private final TicketMapper ticketMapper;

    public TicketRepositoryImpl(TicketJpaRepository ticketJpaRepository, TicketMapper ticketMapper) {
        this.ticketJpaRepository = ticketJpaRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity ticketEntity = ticketMapper.toEntity(ticket);
        TicketEntity savedEntity = ticketJpaRepository.save(ticketEntity);
        return ticketMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return ticketMapper.toDomainOptional(ticketJpaRepository.findById(id));
    }

    @Override
    public Optional<Ticket> findByVeiculoPlacaAndStatusNotFinalizado(String placa) {
        return ticketMapper.toDomainOptional(ticketJpaRepository.findByVeiculoPlacaAndStatusNot(placa, Status.FINALIZADO));
    }

    @Override
    public void delete(Ticket ticket) {
        TicketEntity ticketEntity = ticketMapper.toEntity(ticket);
        ticketJpaRepository.delete(ticketEntity);
    }
}
