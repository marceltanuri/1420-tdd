package io.github.marceltanuri.estacionamento.domain.ticket.ports;

import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import java.util.Optional;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(String id);

    Optional<Ticket> findByVeiculoPlacaAndStatusNotFinalizado(String placa);

    void delete(Ticket ticket);


}