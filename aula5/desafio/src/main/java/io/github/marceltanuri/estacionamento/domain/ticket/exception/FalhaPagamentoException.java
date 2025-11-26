package io.github.marceltanuri.estacionamento.domain.ticket.exception;

public class FalhaPagamentoException extends RuntimeException {
    public FalhaPagamentoException(Throwable cause) {
        super("Erro ao processar o pagamento.", cause);
    }
}
