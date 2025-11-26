package io.github.marceltanuri.estacionamento.domain.veiculo;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.util.Objects;
import java.util.regex.Pattern;

@Entity
public class Veiculo {

    @Id
    private String placa;
    @Enumerated(EnumType.STRING)
    private TipoVeiculo tipo;

    private static final Pattern PLACA_PADRAO_MERCOSUL = Pattern.compile("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}");

    // Default constructor for JPA
    public Veiculo() {
    }

    public Veiculo(String placa, TipoVeiculo tipo) {
        Objects.requireNonNull(placa, "A placa não pode ser nula.");
        if (placa.isBlank()) {
            throw new IllegalArgumentException("A placa não pode estar em branco.");
        }
        if (!PLACA_PADRAO_MERCOSUL.matcher(placa).matches()) {
            throw new IllegalArgumentException("Placa inválida.");
        }
        this.placa = placa;
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public enum TipoVeiculo {
        CAMINHAO, CARRO, MOTO
    }
}