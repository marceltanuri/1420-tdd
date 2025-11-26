package io.github.marceltanuri.estacionamento.domain.ticket;

public enum Status {
    PENDENTE, PAGO, ISENTO, ISENTO_FUNCIONARIO, TOLERANCIA_APOS_PAGAMENTO_EXPIRADO, TOLERANCIA_APOS_ISENCAO_EXPIRADO, FINALIZADO;
}
