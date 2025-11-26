package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.ticket.Status;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.FuncionarioRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.ValidadorComprovante;
import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IsencaoServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;
    @Mock
    private ValidadorComprovante validadorComprovante;

    @InjectMocks
    private IsencaoService isencaoService;

    private Ticket ticket;
    private Veiculo veiculo;
    private final LocalDateTime FIXED_ENTRY_TIME = LocalDateTime.of(2025, 11, 25, 10, 0, 0);

    @BeforeEach
    void setUp() {
        veiculo = new Veiculo("ABC1234", Veiculo.TipoVeiculo.CARRO);
        ticket = Ticket.novo(veiculo, FIXED_ENTRY_TIME);
    }

    @Test
    @DisplayName("Deve considerar isento se comprovante de compra válido")
    void deveIsentarSeComprovanteDeCompraValido() {
        // GIVEN
        String comprovanteValido = "comprovante123";
        when(validadorComprovante.validar(comprovanteValido)).thenReturn(true);

        // WHEN
        isencaoService.isentarPorComprovanteDeCompra(ticket, comprovanteValido);

        // THEN
        assertEquals(Status.ISENTO, ticket.getStatus());
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    @DisplayName("Não deve isentar se comprovante de compra inválido")
    void naoDeveIsentarSeComprovanteDeCompraInvalido() {
        // GIVEN
        String comprovanteInvalido = "comprovanteInvalido";
        when(validadorComprovante.validar(comprovanteInvalido)).thenReturn(false);

        // WHEN & THEN
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            isencaoService.isentarPorComprovanteDeCompra(ticket, comprovanteInvalido);
        });
        assertEquals("Comprovante inválido.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticket.getStatus());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Deve considerar isento se placa cadastrada de funcionário")
    void deveIsentarSePlacaDeFuncionario() {
        // GIVEN
        Veiculo veiculoFuncionario = new Veiculo("FUN0001", Veiculo.TipoVeiculo.CARRO);
        Ticket ticketFuncionario = Ticket.novo(veiculoFuncionario, FIXED_ENTRY_TIME);
        when(funcionarioRepository.isFuncionario(veiculoFuncionario.getPlaca())).thenReturn(true);

        // WHEN
        isencaoService.isentarFuncionario(ticketFuncionario);

        // THEN
        assertEquals(Status.ISENTO_FUNCIONARIO, ticketFuncionario.getStatus());
        verify(ticketRepository, times(1)).save(ticketFuncionario);
    }

    @Test
    @DisplayName("Não deve isentar se placa não pertence a um funcionário")
    void naoDeveIsentarSePlacaNaoDeFuncionario() {
        // GIVEN
        Veiculo veiculoNaoFuncionario = new Veiculo("NUL0001", Veiculo.TipoVeiculo.CARRO);
        Ticket ticketNaoFuncionario = Ticket.novo(veiculoNaoFuncionario, FIXED_ENTRY_TIME);
        when(funcionarioRepository.isFuncionario(veiculoNaoFuncionario.getPlaca())).thenReturn(false);

        // WHEN & THEN
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            isencaoService.isentarFuncionario(ticketNaoFuncionario);
        });
        assertEquals("Placa não pertence a um funcionário.", thrown.getMessage());
        assertEquals(Status.PENDENTE, ticketNaoFuncionario.getStatus());
        verify(ticketRepository, never()).save(any(Ticket.class));
    }
}
