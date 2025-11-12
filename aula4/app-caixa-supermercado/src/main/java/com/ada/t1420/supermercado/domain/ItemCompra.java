package com.ada.t1420.supermercado.domain;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Representa um item individual dentro de um carrinho de compras ou pedido.
 * Responsável por calcular seu próprio subtotal.
 */
public class ItemCompra {

    private final Produto produto;
    private final int quantidade;
    private final BigDecimal subtotal;

    /**
     * Construtor para ItemCompra.
     * * @param produto O produto que está sendo comprado.
     * @param quantidade A quantidade do produto. Deve ser maior que zero.
     * @throws IllegalArgumentException se o produto for nulo ou a quantidade for zero ou negativa.
     */
    public ItemCompra(Produto produto, int quantidade) {
        
        this.produto = Objects.requireNonNull(produto, "O produto do item de compra não pode ser nulo.");

        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade do item de compra deve ser estritamente positiva (maior que zero).");
        }
        this.quantidade = quantidade;

        this.subtotal = produto.getPrecoUnitario()
                               .multiply(new BigDecimal(quantidade))
                               .setScale(2, RoundingMode.HALF_EVEN);
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}