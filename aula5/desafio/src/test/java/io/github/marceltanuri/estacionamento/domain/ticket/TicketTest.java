package io.github.marceltanuri.estacionamento.domain.ticket;

import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TicketTest {

    private Veiculo veiculo;
    private LocalDateTime entrada;

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo("DEF5678", Veiculo.TipoVeiculo.MOTO);
        entrada = LocalDateTime.of(2025, 12, 1, 9, 0, 0);
    }

    @Test
    @DisplayName("Deve criar um novo ticket com status PENDENTE e data de entrada")
    void deveCriarNovoTicketComStatusPendenteEDataDeEntrada() {
        Ticket ticket = Ticket.novo(veiculo, entrada);

        assertNotNull(ticket);
        assertEquals(veiculo, ticket.getVeiculo());
        assertEquals(entrada, ticket.getEntrada());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        assertNull(ticket.getPagamento());
        assertNull(ticket.getSaida());
    }

    @Test
    @DisplayName("Deve pagar um ticket e alterar seu status para PAGO com a data de pagamento")
    void devePagarTicketEAlterarStatusParaPago() {
        Ticket ticket = Ticket.novo(veiculo, entrada);
        LocalDateTime pagamento = LocalDateTime.of(2025, 12, 1, 10, 0, 0);

        ticket.pagar(pagamento);

        assertEquals(Status.PAGO, ticket.getStatus());
        assertEquals(pagamento, ticket.getPagamento());
    }

    @Test
    @DisplayName("Não deve pagar um ticket já finalizado")
    void naoDevePagarTicketJaFinalizado() {
        Ticket ticket = Ticket.novo(veiculo, entrada);
        ticket.finalizar(LocalDateTime.now());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                ticket.pagar(LocalDateTime.now()));

        assertTrue(exception.getMessage().contains("não pode ser pago"));
    }

    @Test
    @DisplayName("Deve expirar pagamento e mudar status para TOLERANCIA_APOS_PAGAMENTO_EXPIRADO")
    void deveExpirarPagamento() {
        Ticket ticket = Ticket.novo(veiculo, entrada);
        ticket.pagar(LocalDateTime.now()); // Ticket must be PAGO to expire payment

        ticket.expirarPagamento();

        assertEquals(Status.TOLERANCIA_APOS_PAGAMENTO_EXPIRADO, ticket.getStatus());
    }

    @Test
    @DisplayName("Não deve expirar pagamento se ticket não estiver PAGO")
    void naoDeveExpirarPagamentoSeNaoEstiverPago() {
        Ticket ticket = Ticket.novo(veiculo, entrada); // Status PENDENTE

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                ticket.expirarPagamento());

        assertTrue(exception.getMessage().contains("não pode ser expirado"));
        assertEquals(Status.PENDENTE, ticket.getStatus());
    }

    @Test
    @DisplayName("Deve expirar isenção e mudar status para TOLERANCIA_APOS_ISENCAO_EXPIRADO")
    void deveExpirarIsencao() {
        Ticket ticket = Ticket.novo(veiculo, entrada);
        ticket.isentar(Status.ISENTO); // Ticket must be ISENTO to expire exemption

        ticket.expirarIsencao();

        assertEquals(Status.TOLERANCIA_APOS_ISENCAO_EXPIRADO, ticket.getStatus());
    }

    @Test
    @DisplayName("Não deve expirar isenção se ticket não estiver ISENTO")
    void naoDeveExpirarIsencaoSeNaoEstiverIsento() {
        Ticket ticket = Ticket.novo(veiculo, entrada); // Status PENDENTE

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                ticket.expirarIsencao());

        assertTrue(exception.getMessage().contains("não pode ser expirado"));
        assertEquals(Status.PENDENTE, ticket.getStatus());
    }

    @Test
    @DisplayName("Deve finalizar um ticket e alterar seu status para FINALIZADO com a data de saída")
    void deveFinalizarTicketEAlterarStatusParaFinalizado() {
        Ticket ticket = Ticket.novo(veiculo, entrada);
        LocalDateTime saida = LocalDateTime.of(2025, 12, 1, 11, 0, 0);

        ticket.finalizar(saida);

        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saida, ticket.getSaida());
    }

    @Test
    @DisplayName("Não deve finalizar um ticket já finalizado")
    void naoDeveFinalizarTicketJaFinalizado() {
        Ticket ticket = Ticket.novo(veiculo, entrada);
        LocalDateTime saida = LocalDateTime.of(2025, 12, 1, 11, 0, 0);
        ticket.finalizar(saida); // First finalization

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                ticket.finalizar(LocalDateTime.now()));

        assertTrue(exception.getMessage().contains("já finalizado"));
        assertEquals(Status.FINALIZADO, ticket.getStatus());
        assertEquals(saida, ticket.getSaida()); // Saida date should not change
    }

    @Test
    @DisplayName("Deve isentar um ticket com status ISENTO")
    void deveIsentarTicketComStatusIsento() {
        Ticket ticket = Ticket.novo(veiculo, entrada);

        ticket.isentar(Status.ISENTO);

        assertEquals(Status.ISENTO, ticket.getStatus());
    }

    @Test
    @DisplayName("Deve isentar um ticket com status ISENTO_FUNCIONARIO")
    void deveIsentarTicketComStatusIsentoFuncionario() {
        Ticket ticket = Ticket.novo(veiculo, entrada);

        ticket.isentar(Status.ISENTO_FUNCIONARIO);

        assertEquals(Status.ISENTO_FUNCIONARIO, ticket.getStatus());
    }

    @Test
    @DisplayName("Não deve isentar um ticket com status inválido")
    void naoDeveIsentarTicketComStatusInvalido() {
        Ticket ticket = Ticket.novo(veiculo, entrada);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                ticket.isentar(Status.PAGO)); // Attempt to isentar with a non-exemption status

        assertTrue(exception.getMessage().contains("Status de isenção inválido"));
        assertEquals(Status.PENDENTE, ticket.getStatus()); // Status should remain PENDENTE
    }

    @Test
    @DisplayName("Não deve isentar um ticket já finalizado")
    void naoDeveIsentarTicketJaFinalizado() {
        Ticket ticket = Ticket.novo(veiculo, entrada);
        ticket.finalizar(LocalDateTime.now());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                ticket.isentar(Status.ISENTO));

        assertTrue(exception.getMessage().contains("não pode ser isentado"));
        assertEquals(Status.FINALIZADO, ticket.getStatus());
    }
}