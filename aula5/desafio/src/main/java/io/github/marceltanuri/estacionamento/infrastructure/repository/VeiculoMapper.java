package io.github.marceltanuri.estacionamento.infrastructure.repository;

import io.github.marceltanuri.estacionamento.domain.veiculo.Veiculo;
import io.github.marceltanuri.estacionamento.infrastructure.persistence.VeiculoEntity;
import org.springframework.stereotype.Component;

@Component
public class VeiculoMapper {

    public VeiculoEntity toEntity(Veiculo domainVeiculo) {
        if (domainVeiculo == null) {
            return null;
        }
        return new VeiculoEntity(domainVeiculo.getPlaca(), domainVeiculo.getTipo());
    }

    public Veiculo toDomain(VeiculoEntity entityVeiculo) {
        if (entityVeiculo == null) {
            return null;
        }
        return new Veiculo(entityVeiculo.getPlaca(), entityVeiculo.getTipo());
    }
}
