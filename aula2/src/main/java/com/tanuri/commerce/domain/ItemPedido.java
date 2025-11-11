package com.tanuri.commerce.domain;



public class ItemPedido {
    private Produto produto;
    private int quantidade;
    
    public ItemPedido(Produto produto, int quantidade) {
    }

    public double getSubtotal() {
        return 0.0;
    }

    public Produto getProduto() {
        return produto;
    }   

    public int getQuantidade() {
        return quantidade;
    }

    
    }