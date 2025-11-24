package io.github.marceltanuri.estacionamento.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TicketTest {

    @Test
    void deveEmitirTicketParaVeiculoNovo() {
        // given
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        List<Ticket> ticketsAtivos = new ArrayList<>();

        ContadorTicket idGenerator = new ContadorTicket() {
            @Override
            public int proximoId() {
                return 1;
            }
        };

        // when
        Ticket ticket = Ticket.emitir(idGenerator, veiculo);

        // then
        assertNotNull(ticket);
        assertEquals("ABC1234", ticket.getVeiculo().placa());
        assertNotNull(ticket.getEntrada());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        assertEquals(1, ticket.getId());
    }

}