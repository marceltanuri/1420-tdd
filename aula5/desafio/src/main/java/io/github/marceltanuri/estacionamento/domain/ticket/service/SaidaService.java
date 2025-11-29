package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.Status;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SaidaService {

    private static final int TOLERANCIA_MINUTOS = 15;
    private static final int LIMITE_PERMANENCIA_POS_PAGAMENTO_MINUTOS = 15;
    private static final int LIMITE_PERMANENCIA_POS_ISENCAO_MINUTOS = 120;
    private static final LocalTime HORARIO_DE_ABERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORARIO_DE_FECHAMENTO = LocalTime.of(22, 0);
    private final TicketRepository ticketRepository;
    private final java.time.Clock clock;

    public SaidaService(TicketRepository ticketRepository, java.time.Clock clock) {
        this.ticketRepository = ticketRepository;
        this.clock = clock;
    }

    public void processarSaida(Ticket ticket) {
        LocalDateTime dataHoraSaida = LocalDateTime.now(clock);
        if (dataHoraSaida.isBefore(ticket.getEntrada())) {
            throw new IllegalStateException("Ticket não pode ser finalizado antes da entrada.");
        }

        if (dataHoraSaida.toLocalTime().isBefore(HORARIO_DE_ABERTURA) || dataHoraSaida.toLocalTime().isAfter(HORARIO_DE_FECHAMENTO)) {
            if (ticket.getStatus() != Status.ISENTO_FUNCIONARIO) {
                throw new IllegalStateException("Ticket não pode ser finalizado fora do horário de funcionamento.");
            }
        }

        if (ticket.getStatus() == Status.PAGO) {
            long minutosAposPagamento = Duration.between(ticket.getPagamento(), dataHoraSaida).toMinutes();
            if (minutosAposPagamento > LIMITE_PERMANENCIA_POS_PAGAMENTO_MINUTOS) {
                ticket.expirarPagamento();
                ticketRepository.save(ticket);
                throw new IllegalStateException("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.");
            }
        }

        if (ticket.getStatus() == Status.ISENTO) {
            long minutosAposIsencao = Duration.between(ticket.getEntrada(), dataHoraSaida).toMinutes();
            if (minutosAposIsencao > LIMITE_PERMANENCIA_POS_ISENCAO_MINUTOS) {
                ticket.expirarIsencao();
                ticketRepository.save(ticket);
                throw new IllegalStateException("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.");
            }
        }

        if (isPeriodoTolerancia(ticket, dataHoraSaida) || ticket.getStatus() == Status.ISENTO || ticket.getStatus() == Status.ISENTO_FUNCIONARIO || ticket.getStatus() == Status.PAGO) {
            ticket.finalizar(dataHoraSaida);
            ticketRepository.save(ticket);
            return;
        }

        throw new IllegalStateException("Ticket com status " + ticket.getStatus() + " não pode ser finalizado.");
    }

    private boolean isPeriodoTolerancia(Ticket ticket, LocalDateTime dataHoraSaida) {
        long minutosEstacionado = Duration.between(ticket.getEntrada(), dataHoraSaida).toMinutes();
        return minutosEstacionado < TOLERANCIA_MINUTOS && ticket.getStatus() == Status.PENDENTE;
    }

}
