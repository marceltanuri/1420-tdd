package com.tanuri.commerce.domain;

public class Produto {

    private String nome;
    private String descricao;
    private double preco;
    private int estoque;

    public Produto(String nome, String descricao, double preco, int estoque) {
        // Validações no Construtor (Garante que o objeto nasce válido)
        validarNome(nome);
        validarDescricao(descricao);
        validarPreco(preco);
        validarEstoque(estoque);

        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
    }

    // --- Getters ---

    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public int getEstoque() { return estoque; }

    // --- Setters com Validação ---

    public void setNome(String nome) {
        validarNome(nome);
        this.nome = nome;
    }

    public void setDescricao(String descricao) {
        validarDescricao(descricao);
        this.descricao = descricao;
    }

    public void setPreco(double preco) {
        validarPreco(preco); // Valida antes de alterar
        this.preco = preco;
    }

    public void setEstoque(int estoque) {
        validarEstoque(estoque); // Valida antes de alterar
        this.estoque = estoque;
    }

    // --- Métodos de Validação de Invariantes ---

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser nula ou vazia");
        }
    }

    private void validarPreco(double preco) {
        if (preco <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
    }

    private void validarEstoque(int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }
    }

    public void darBaixaEstoque(int quantidade) {
        if (quantidade > this.estoque) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }
        this.estoque -= quantidade;

    }
}