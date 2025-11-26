package io.github.marceltanuri.estacionamento.domain.ticket.ports;

public interface FuncionarioRepository {
    boolean isFuncionario(String placa);
}
