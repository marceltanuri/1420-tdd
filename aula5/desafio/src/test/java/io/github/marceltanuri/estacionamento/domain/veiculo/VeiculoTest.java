package io.github.marceltanuri.estacionamento.domain.veiculo;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class VeiculoTest {
    @Test
    void deveLancarExcecaoParaPlacaNula() {
        assertThrows(NullPointerException.class, () -> {
            new Veiculo(null, Veiculo.TipoVeiculo.CARRO);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void deveLancarExcecaoParaPlacaEmBranco(String placa) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Veiculo(placa, Veiculo.TipoVeiculo.CARRO);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC123", "123ABCD"})
    void deveLancarExcecaoParaPlacaComFormatoInvalido(String placa) {
        assertThrows(IllegalArgumentException.class, () -> {
            new Veiculo(placa, Veiculo.TipoVeiculo.CARRO);
        });
    }
}
