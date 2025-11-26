package io.github.marceltanuri.estacionamento.domain.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketJpaRepository extends JpaRepository<Ticket, String> {
    Optional<Ticket> findByVeiculoPlacaAndStatusNot(String placa, Status status);
}
