package io.github.marceltanuri.estacionamento.domain;

public record Veiculo(String placa, TipoVeiculo tipo) {
    public enum TipoVeiculo {
        CAMINHAO, CARRO, MOTO
    }
}