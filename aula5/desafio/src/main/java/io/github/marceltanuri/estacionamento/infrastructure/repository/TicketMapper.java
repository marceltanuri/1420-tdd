package io.github.marceltanuri.estacionamento.infrastructure.repository;

import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import io.github.marceltanuri.estacionamento.infrastructure.persistence.TicketEntity;
import io.github.marceltanuri.estacionamento.infrastructure.persistence.VeiculoEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TicketMapper {

    private final VeiculoMapper veiculoMapper;

    public TicketMapper(VeiculoMapper veiculoMapper) {
        this.veiculoMapper = veiculoMapper;
    }

    public TicketEntity toEntity(Ticket domainTicket) {
        if (domainTicket == null) {
            return null;
        }
        TicketEntity entity = new TicketEntity();
        entity.setId(domainTicket.getId());
        entity.setVeiculo(veiculoMapper.toEntity(domainTicket.getVeiculo()));
        entity.setEntrada(domainTicket.getEntrada());
        entity.setSaida(domainTicket.getSaida());
        entity.setPagamento(domainTicket.getPagamento());
        entity.setStatus(domainTicket.getStatus());
        return entity;
    }

    public Ticket toDomain(TicketEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Ticket(
                entity.getId(),
                veiculoMapper.toDomain(entity.getVeiculo()),
                entity.getEntrada(),
                entity.getSaida(),
                entity.getPagamento(),
                entity.getStatus()
        );
    }

    public Optional<Ticket> toDomainOptional(Optional<TicketEntity> entityOptional) {
        return entityOptional.map(this::toDomain);
    }
}
