package io.github.marceltanuri.estacionamento.domain.ticket.ports;

import java.math.BigDecimal;

public interface OperadoraPagamento {
    void pagar(BigDecimal valor);
}