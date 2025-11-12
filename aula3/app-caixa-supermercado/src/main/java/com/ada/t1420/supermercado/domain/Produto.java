package com.ada.t1420.supermercado.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa um produto com código, nome e preço unitário.
 */
public class Produto {

    private final String codigo;
    private final String nome;
    private final BigDecimal precoUnitario;

    private static final String MENSAGEM_ERRO_PRECO_UNITARIO = "Preço unitário deve ser estritamente positivo";


    /**
     * Construtor da classe Produto.
     * @param codigo O código único do produto.
     * @param nome O nome do produto.
     * @param precoUnitario O preço unitário do produto. Deve ser estritamente positivo (maior que zero).
     * @throws IllegalArgumentException se o precoUnitario for nulo, zero ou negativo.
     */
    public Produto(String codigo, String nome, BigDecimal precoUnitario) {
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(MENSAGEM_ERRO_PRECO_UNITARIO);
        }

        this.codigo = Objects.requireNonNull(codigo, "O código não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "O nome não pode ser nulo.");
        this.precoUnitario = precoUnitario;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
}