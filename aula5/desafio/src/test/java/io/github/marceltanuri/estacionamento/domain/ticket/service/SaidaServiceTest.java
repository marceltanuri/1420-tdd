package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.ticket.Status;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaidaServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private SaidaService saidaService;

    private Ticket ticket;
    private Veiculo veiculo;
    private final LocalDateTime FIXED_ENTRY_TIME = LocalDateTime.of(2025, 11, 25, 10, 0, 0);

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        ticket = Ticket.novo(veiculo, FIXED_ENTRY_TIME);
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for ISENTO")
    void deveFinalizarTicketIsento() {
        // GIVEN
        ticket.isentar(Status.ISENTO);


        // WHEN
        saidaService.processarSaida(ticket, FIXED_ENTRY_TIME);

        // THEN       
        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(FIXED_ENTRY_TIME, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for PAGO e saída em até 15 minutos do pagamento")
    void deveFinalizarTicketPagoDentroDoLimite() {
        // GIVEN
        LocalDateTime paymentTime = FIXED_ENTRY_TIME.plusHours(1);
        ticket.pagar(paymentTime);
        LocalDateTime saidaTime = paymentTime.plusMinutes(14); // 14 minutes after payment

        // WHEN
        saidaService.processarSaida(ticket, saidaTime);

        // THEN
        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve alterar o status para FINALIZADO se hora atual for fora do horário de funcionamento (exceto funcionário)")
    void naoDeveFinalizarForaDoHorarioDeFuncionamento() {
        // GIVEN
        LocalDateTime saidaTime = LocalDateTime.of(2025, 11, 25, 22, 1, 0); // After 22:00 closing time
        ticket.isentar(Status.ISENTO); // Can be any status other than ISENTO_FUNCIONARIO

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket, saidaTime);
        });

        assertEquals("Ticket não pode ser finalizado fora do horário de funcionamento.", thrown.getMessage());
        assertNotEquals(Status.FINALIZADO, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for ISENTO_FUNCIONARIO, mesmo fora do horário")
    void deveFinalizarTicketIsentoFuncionarioMesmoForaDoHorario() {
        // GIVEN
        ticket.isentar(Status.ISENTO_FUNCIONARIO);
        LocalDateTime saidaTime = LocalDateTime.of(2025, 11, 25, 23, 0, 0); // Outside operational hours

        // WHEN
        saidaService.processarSaida(ticket, saidaTime);

        // THEN
        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Deve finalizar ticket PENDENTE se permanência menor que 15 minutos (tolerância)")
    void deveFinalizarTicketPendenteSeDentroTolerancia() {
        // GIVEN
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(14); // 14 minutes, within tolerance

        // WHEN
        saidaService.processarSaida(ticket, saidaTime);

        // THEN
        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve finalizar ticket PENDENTE se permanência maior ou igual a 15 minutos (fora tolerância)")
    void naoDeveFinalizarTicketPendenteSeForaTolerancia() {
        // GIVEN
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(15); // 15 minutes, exactly at tolerance limit

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket, saidaTime);
        });

        assertEquals("Ticket com status PENDENTE não pode ser finalizado.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void deveExpirarPagamentoSeExcederLimite() {
        // GIVEN
        LocalDateTime paymentTime = FIXED_ENTRY_TIME.plusHours(1);
        ticket.pagar(paymentTime); // Status becomes PAGO
        LocalDateTime saidaTime = paymentTime.plusMinutes(16); // 16 minutes after payment, exceeding limit

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket, saidaTime);
        });

        assertEquals("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.", thrown.getMessage());
        assertEquals(Status.TOLERANCIA_APOS_PAGAMENTO_EXPIRADO, ticket.getStatus()); 
        assertNull(ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket); 
    }

    @Test
    void deveExpirarIsencaoSeExcederLimite() {
        // GIVEN
        ticket.isentar(Status.ISENTO);
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(121); // 121 minutes after entry, exceeding limit

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket, saidaTime);
        });

        assertEquals("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.", thrown.getMessage());
        assertEquals(Status.TOLERANCIA_APOS_ISENCAO_EXPIRADO, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket); 
    }

    @Test
    @DisplayName("Não deve permitir saída se data e hora de saída for anterior à entrada")
    void naoDevePermitirSaidaAnteriorAEntrada() {
        // GIVEN
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.minusMinutes(1);

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket, saidaTime);
        });

        assertEquals("Ticket não pode ser finalizado antes da entrada.", thrown.getMessage());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Não deve finalizar ticket com status inválido para saída")
    void naoDeveFinalizarTicketComStatusInvalido() {
        // GIVEN
        // Ticket is PENDENTE by default, but outside tolerance (e.g., 20 mins)
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(20);

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket, saidaTime);
        });

        assertEquals("Ticket com status PENDENTE não pode ser finalizado.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

}
