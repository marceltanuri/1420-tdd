package io.github.marceltanuri.estacionamento.domain.ticket.ports;

public interface ValidadorComprovante{

    boolean validar(String comprovante);


}