package io.github.marceltanuri.estacionamento.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo; // Import for TipoVeiculo

@Entity
@Table(name = "veiculos") // Assuming a table named 'veiculos' for vehicle entities
public class VeiculoEntity {

    @Id
    private String placa;

    @Enumerated(EnumType.STRING)
    private Veiculo.TipoVeiculo tipo; // Using the domain enum for consistency, but could also define a separate enum in infra.

    public VeiculoEntity() {
    }

    public VeiculoEntity(String placa, Veiculo.TipoVeiculo tipo) {
        this.placa = placa;
        this.tipo = tipo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Veiculo.TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(Veiculo.TipoVeiculo tipo) {
        this.tipo = tipo;
    }
}
