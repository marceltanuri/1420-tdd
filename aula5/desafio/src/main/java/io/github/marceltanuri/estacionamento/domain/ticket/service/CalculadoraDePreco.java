package io.github.marceltanuri.estacionamento.domain.ticket.service;

import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import io.github.marceltanuri.estacionamento.domain.ticket.Ticket;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class CalculadoraDePreco {

    private static final BigDecimal TARIFA_BASE_HORA_CARRO = new BigDecimal("10.00");
    private static final BigDecimal TARIFA_ADICIONAL_HORA_CARRO = new BigDecimal("5.00");
    private static final BigDecimal DIARIA_CARRO = new BigDecimal("50.00");
    private static final BigDecimal TARIFA_BASE_HORA_MOTO = new BigDecimal("5.00");
    private static final BigDecimal TARIFA_ADICIONAL_HORA_MOTO = new BigDecimal("2.50");


    public BigDecimal calcular(Ticket ticket, LocalDateTime dataHoraSaida) {
        LocalDateTime entrada = ticket.getEntrada();
        long horas = (long) Math.ceil(Duration.between(entrada, dataHoraSaida).toMinutes() / 60.0);

        if (horas <= 0) {
            horas = 1;
        }

        return switch (ticket.getVeiculo().getTipo()) {
            case CARRO -> calcularPrecoCarro(horas);
            case MOTO -> calcularPrecoMoto(horas);
            default -> BigDecimal.ZERO;
        };
    }

    private BigDecimal calcularPrecoCarro(long horas) {
        if (horas > 8) {
            return DIARIA_CARRO;
        }
        if (horas <= 2) {
            return TARIFA_BASE_HORA_CARRO.multiply(BigDecimal.valueOf(horas));
        }
        return TARIFA_BASE_HORA_CARRO.multiply(BigDecimal.valueOf(2))
                .add(TARIFA_ADICIONAL_HORA_CARRO.multiply(BigDecimal.valueOf(horas - 2)));
    }

    private BigDecimal calcularPrecoMoto(long horas) {
        if (horas <= 2) {
            return TARIFA_BASE_HORA_MOTO.multiply(BigDecimal.valueOf(horas));
        }
        return TARIFA_BASE_HORA_MOTO.multiply(BigDecimal.valueOf(2))
                .add(TARIFA_ADICIONAL_HORA_MOTO.multiply(BigDecimal.valueOf(horas - 2)));
    }

}
