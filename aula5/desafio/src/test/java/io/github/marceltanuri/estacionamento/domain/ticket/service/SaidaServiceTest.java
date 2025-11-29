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
import java.time.ZoneId;
import java.time.Instant;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaidaServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    // O Mock do Clock será usado para controlar o tempo
    @Mock
    private java.time.Clock clock;

    @InjectMocks
    private SaidaService saidaService;

    // Apenas a entrada é fixa para todos os testes
    private final LocalDateTime FIXED_ENTRY_TIME = LocalDateTime.of(2025, 11, 25, 10, 0, 0);

    private Ticket ticket;
    private Veiculo veiculo;

    @BeforeEach
    void setUp() {
        // Não precisamos mais de um FIXED_CLOCK local.
        // Apenas definimos o Veiculo e o Ticket de entrada.
        veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        // Assumindo que Ticket.novo usa FIXED_ENTRY_TIME como entrada
        ticket = Ticket.novo(veiculo, FIXED_ENTRY_TIME);
    }

    /**
     * Helper para configurar o Mock do Clock em cada teste.
     * @param simulatedTime A hora que o Clock deve retornar.
     */
    private void setupClock(LocalDateTime simulatedTime) {
        // Converte o LocalDateTime para Instant e configura o mock
        Instant instant = simulatedTime.atZone(ZoneId.systemDefault()).toInstant();
        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for ISENTO")
    void deveFinalizarTicketIsento() {
        // GIVEN
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(60); // Saída dentro do limite de 120min
        setupClock(saidaTime); // Configura o Clock para a hora de saída
        ticket.isentar(Status.ISENTO);

        // WHEN
        saidaService.processarSaida(ticket);

        // THEN       
        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida()); // Saída deve ser a hora simulada
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Deve alterar o status para FINALIZADO se status for PAGO e saída em até 15 minutos do pagamento")
    void deveFinalizarTicketPagoDentroDoLimite() {
        // GIVEN
        LocalDateTime paymentTime = FIXED_ENTRY_TIME.plusHours(1);
        ticket.pagar(paymentTime);
        LocalDateTime saidaTime = paymentTime.plusMinutes(14); // 14 minutes after payment (DENTRO do limite)
        setupClock(saidaTime); // Configura o Clock para a hora de saída

        // WHEN
        saidaService.processarSaida(ticket);

        // THEN
        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saidaTime, ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve alterar o status para FINALIZADO se hora atual for fora do horário de funcionamento (exceto funcionário)")
    void naoDeveFinalizarForaDoHorarioDeFuncionamento() {
        // GIVEN
        // Fora do horário (depois das 22:00)
        LocalDateTime saidaTime = LocalDateTime.of(2025, 11, 25, 22, 1, 0); 
        setupClock(saidaTime); // Configura o Clock para 22:01
        
        ticket.isentar(Status.ISENTO); // Qualquer status diferente de ISENTO_FUNCIONARIO

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
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
        // Hora fora do expediente (ex: 23:00)
        LocalDateTime saidaTime = LocalDateTime.of(2025, 11, 25, 23, 0, 0); 
        setupClock(saidaTime); // Configura o Clock para 23:00

        // WHEN
        saidaService.processarSaida(ticket);

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
        setupClock(saidaTime); // Configura o Clock para 10:14

        // WHEN
        saidaService.processarSaida(ticket);

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
        setupClock(saidaTime); // Configura o Clock para 10:15

        // WHEN & THEN
        // Agora, o serviço deve lançar exceção porque está fora do período de tolerância e o status é PENDENTE
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        // Este é o status de erro esperado para PENDENTE fora da tolerância
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
        setupClock(saidaTime); // Configura o Clock para a hora de saída

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.", thrown.getMessage());
        // Assumindo que este é o status de expiração que você usa no seu domínio
        assertEquals(Status.TOLERANCIA_APOS_PAGAMENTO_EXPIRADO, ticket.getStatus()); 
        assertNull(ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket); 
    }

    @Test
    void deveExpirarIsencaoSeExcederLimite() {
        // GIVEN
        ticket.isentar(Status.ISENTO);
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.plusMinutes(121); // 121 minutes after entry, exceeding limit
        setupClock(saidaTime); // Configura o Clock para a hora de saída

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket não pode ser finalizado após o limite de tolerância. Necessário novo pagamento.", thrown.getMessage());
        // Assumindo que este é o status de expiração que você usa no seu domínio
        assertEquals(Status.TOLERANCIA_APOS_ISENCAO_EXPIRADO, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, times(1)).save(ticket); 
    }

    @Test
    @DisplayName("Não deve permitir saída se data e hora de saída for anterior à entrada")
    void naoDevePermitirSaidaAnteriorAEntrada() {
        // GIVEN
        LocalDateTime saidaTime = FIXED_ENTRY_TIME.minusMinutes(1);
        setupClock(saidaTime); // Configura o Clock para a hora anterior

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
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
        setupClock(saidaTime); // Configura o Clock para a hora de saída

        // WHEN & THEN
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            saidaService.processarSaida(ticket);
        });

        assertEquals("Ticket com status PENDENTE não pode ser finalizado.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        assertNull(ticket.getSaida());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}