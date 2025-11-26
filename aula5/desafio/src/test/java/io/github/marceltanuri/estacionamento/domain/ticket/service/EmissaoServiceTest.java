package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import io.github.marceltanuri.estacionamento.domain.ticket.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmissaoServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private Clock clock;

    @InjectMocks
    private EmissaoService emissaoService;

    private final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(2025, 11, 25, 10, 0, 0);
    private final Clock FIXED_CLOCK = Clock.fixed(Instant.from(FIXED_DATE_TIME.atZone(ZoneId.systemDefault())), ZoneId.systemDefault());

    @BeforeEach
    void setUp() {
        // Ensure the clock mock is used by EmissaoService
        lenient().when(clock.instant()).thenReturn(FIXED_CLOCK.instant());
        lenient().when(clock.getZone()).thenReturn(FIXED_CLOCK.getZone());
    }

    @Test
    @DisplayName("Deve emitir um ticket com ID, data e hora atual e placa do veículo")
    void deveEmitirUmTicketComIdDataHoraAtualEPlacaDoVeiculo() {
        // GIVEN
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        when(ticketRepository.findByVeiculoPlacaAndStatusNotFinalizado(veiculo.getPlaca())).thenReturn(Optional.empty());
        
        Ticket expectedTicket = Ticket.novo(veiculo, FIXED_DATE_TIME);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(expectedTicket);

        // WHEN
        Ticket result = emissaoService.emitir(veiculo);

        // THEN
        assertNotNull(result);
        assertEquals(veiculo.getPlaca(), result.getVeiculo().getPlaca());
        assertEquals(FIXED_DATE_TIME, result.getEntrada());
        assertEquals(Status.PENDENTE, result.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Deve retornar um ticket do banco de dados caso o ticket ainda não conste 'deu saída'")
    void deveRetornarTicketExistenteSeNaoConstaSaida() {
        // GIVEN
        Veiculo veiculo = new Veiculo("XYZ9876", Veiculo.TipoVeiculo.CARRO);
        Ticket existingTicket = Ticket.novo(veiculo, FIXED_DATE_TIME);
        when(ticketRepository.findByVeiculoPlacaAndStatusNotFinalizado(veiculo.getPlaca())).thenReturn(Optional.of(existingTicket));

        // WHEN
        Ticket result = emissaoService.emitir(veiculo);

        // THEN
        assertNotNull(result);
        assertEquals(existingTicket.getId(), result.getId());
        assertEquals(existingTicket.getVeiculo().getPlaca(), result.getVeiculo().getPlaca());
        assertEquals(existingTicket.getEntrada(), result.getEntrada());
        assertEquals(existingTicket.getStatus(), result.getStatus());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
