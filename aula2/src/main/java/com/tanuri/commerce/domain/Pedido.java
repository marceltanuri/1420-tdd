package com.tanuri.commerce.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Pedido{
    private List<ItemPedido> itens;

    public Pedido(ItemPedido... itens) {
        this.itens = new ArrayList<>(Arrays.asList(itens));
    }

    public Pedido(List<ItemPedido> itens) {
        this.itens = new ArrayList<>(itens);
    }

    public double getTotal() {
        return 0.0;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

}