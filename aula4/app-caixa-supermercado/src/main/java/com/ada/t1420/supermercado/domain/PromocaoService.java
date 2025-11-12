package com.ada.t1420.supermercado.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface de serviço para a lógica de aplicação de promoções.
 * Define o contrato para a camada de serviço de promoções.
 */
public interface PromocaoService {

    /**
     * Calcula o desconto total aplicando as promoções ativas a uma lista de itens de compra.
     *
     * @param itens A lista de itens da compra.
     * @return O valor total do desconto calculado.
     */
    BigDecimal aplicarPromocoes(List<ItemCompra> itens);
}