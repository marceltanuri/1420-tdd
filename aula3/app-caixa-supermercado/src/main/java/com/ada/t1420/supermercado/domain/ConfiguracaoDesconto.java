package com.ada.t1420.supermercado.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa uma configuração de desconto baseada em dados.
 * Aplica um percentual de desconto se a quantidade mínima for atingida
 * dentro do período de vigência.
 */
public class ConfiguracaoDesconto {
    
    // Identificador do produto
    private final String skuProduto; 
    
    // Período de validade da promoção
    private final LocalDate dataVigenciaInicio;
    private final LocalDate dataVigenciaFim;
    
    // Condição para ativar a promoção
    private final int quantidadeMinima;          
    
    // O valor do desconto a ser aplicado (Ex: 0.10 para 10%)
    private final BigDecimal percentualDesconto; 

    /**
     * Construtor para inicializar uma configuração de desconto.
     * * @param skuProduto O SKU do produto.
     * @param dataVigenciaInicio A data de início da validade.
     * @param dataVigenciaFim A data de fim da validade.
     * @param quantidadeMinima A quantidade mínima para que o desconto seja ativado.
     * @param percentualDesconto O percentual de desconto (em formato decimal).
     */
    public ConfiguracaoDesconto(String skuProduto, LocalDate dataVigenciaInicio, LocalDate dataVigenciaFim, 
                                int quantidadeMinima, BigDecimal percentualDesconto) {
        this.skuProduto = skuProduto;
        this.dataVigenciaInicio = dataVigenciaInicio;
        this.dataVigenciaFim = dataVigenciaFim;
        this.quantidadeMinima = quantidadeMinima;
        this.percentualDesconto = percentualDesconto;
    }

    // --- Getters (Acessores) ---

    public String getSkuProduto() {
        return skuProduto;
    }

    public LocalDate getDataVigenciaInicio() {
        return dataVigenciaInicio;
    }

    public LocalDate getDataVigenciaFim() {
        return dataVigenciaFim;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    /**
     * Verifica se a configuração está ativa na data fornecida.
     * @param dataAtual A data de referência.
     * @return true se a regra estiver em vigor, false caso contrário.
     */
    public boolean estaVigente(LocalDate dataAtual) {
        // A data atual deve ser >= data de início E <= data de fim.
        return !dataAtual.isBefore(dataVigenciaInicio) && !dataAtual.isAfter(dataVigenciaFim);
    }
}