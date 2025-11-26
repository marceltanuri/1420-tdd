package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.exception.FalhaPagamentoException;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.OperadoraPagamento;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private OperadoraPagamento operadoraPagamento;
    @Mock
    private CalculadoraDePreco calculadoraDePreco;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private Clock clock;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Ticket ticket;
    private final LocalDateTime FIXED_ENTRY_TIME = LocalDateTime.of(2025, 11, 25, 10, 0, 0);
    private final LocalDateTime FIXED_PAYMENT_TIME = LocalDateTime.of(2025, 11, 25, 16, 30, 0);
    private final Clock FIXED_PAYMENT_CLOCK = Clock.fixed(Instant.from(FIXED_PAYMENT_TIME.atZone(ZoneId.systemDefault())), ZoneId.systemDefault());

    @BeforeEach
    void setUp() {
        Veiculo veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        ticket = Ticket.novo(veiculo, FIXED_ENTRY_TIME);
        when(clock.instant()).thenReturn(FIXED_PAYMENT_CLOCK.instant());
        when(clock.getZone()).thenReturn(FIXED_PAYMENT_CLOCK.getZone());
    }

    @Test
    @DisplayName("Deve integrar com sistema de pagamento e marcar ticket como pago com hora e data de pagamento.")
    void deveMarcarTicketComoPagoEmCasoDeSucesso() {
        // GIVEN
        BigDecimal valorCobranca = new BigDecimal("25.00");
        when(calculadoraDePreco.calcular(any(Ticket.class), any(LocalDateTime.class))).thenReturn(valorCobranca);
        doNothing().when(operadoraPagamento).pagar(valorCobranca);

        // WHEN
        pagamentoService.pagar(ticket);

        // THEN
        assertEquals(io.github.marceltanuri.estacionamento.domain.ticket.Status.PAGO, ticket.getStatus());
        assertEquals(FIXED_PAYMENT_TIME, ticket.getPagamento());
        verify(operadoraPagamento, times(1)).pagar(valorCobranca);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve alterar para pago o status do ticket se pagamento falhar.")
    void naoDeveAlterarStatusSePagamentoFalhar() {
        // GIVEN
        BigDecimal valorCobranca = new BigDecimal("25.00");
        when(calculadoraDePreco.calcular(any(Ticket.class), any(LocalDateTime.class))).thenReturn(valorCobranca);
        doThrow(new RuntimeException("Falha na comunicação com a operadora")).when(operadoraPagamento).pagar(valorCobranca);

        // WHEN & THEN
        FalhaPagamentoException thrown = assertThrows(FalhaPagamentoException.class, () -> {
            pagamentoService.pagar(ticket);
        });

        assertEquals(io.github.marceltanuri.estacionamento.domain.ticket.Status.PENDENTE, ticket.getStatus());
        assertNull(ticket.getPagamento());
        verify(operadoraPagamento, times(1)).pagar(valorCobranca);
        verify(ticketRepository, never()).save(any(Ticket.class));
        //assertTrue(thrown.getMessage().contains("Erro ao processar pagamento"));
    }
}
