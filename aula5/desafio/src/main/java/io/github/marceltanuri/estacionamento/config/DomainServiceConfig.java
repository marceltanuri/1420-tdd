package io.github.marceltanuri.estacionamento.config;

import io.github.marceltanuri.estacionamento.domain.ticket.ports.FuncionarioRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.OperadoraPagamento;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.TicketRepository;
import io.github.marceltanuri.estacionamento.domain.ticket.ports.ValidadorComprovante;
import io.github.marceltanuri.estacionamento.domain.ticket.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
public class DomainServiceConfig {

    @Bean
    public EmissaoService emissaoService(TicketRepository ticketRepository, Clock clock) {
        return new EmissaoService(ticketRepository, clock);
    }

    @Bean
    public PagamentoService pagamentoService(OperadoraPagamento operadora, CalculadoraDePreco calculadoraDePreco,
                                             TicketRepository ticketRepository, Clock clock) {
        return new PagamentoService(operadora, calculadoraDePreco, ticketRepository, clock);
    }

    @Bean
    public SaidaService saidaService(TicketRepository ticketRepository, Clock clock) {
        return new SaidaService(ticketRepository, clock);
    }

    @Bean
    public IsencaoService isencaoService(TicketRepository ticketRepository, FuncionarioRepository funcionarioRepository,
                                         ValidadorComprovante validadorComprovante) {
        return new IsencaoService(ticketRepository, funcionarioRepository, validadorComprovante);
    }

    @Bean
    public CalculadoraDePreco calculadoraDePreco() {
        return new CalculadoraDePreco();
    }

    @Bean
    public OperadoraPagamento operadoraPagamento() {
        return valor -> {
        };
    }

    @Bean
    public FuncionarioRepository funcionarioRepository() {
        return placa -> List.of("GJK8D74").contains(placa);
    }

    @Bean
    public ValidadorComprovante validadorComprovante() {
        return "COMPROVANTE_VALIDO"::equals;
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
