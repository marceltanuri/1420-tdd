package com.tanuri.commerce.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProdutoTest {

    // Teste Básico de Sucesso: Garante que um produto válido pode ser criado
    @Test
    void deveCriarProdutoComTodosOsCamposValidos() {
        // ARRANGE
        double precoEsperado = 150.00;
        int estoqueEsperado = 5;

        // ACT
        Produto produto = new Produto("Livro", "Livro técnico", precoEsperado, estoqueEsperado);

        // ASSERT
        assertNotNull(produto);
        assertEquals(precoEsperado, produto.getPreco());
        assertEquals(estoqueEsperado, produto.getEstoque());
    }

    // --- Casos de Falha de Invariantes de Domínio ---

    @Test
    void produto_nao_deve_ter_nome_vazio() {
        // ACT & ASSERT: Verifica se o construtor lança a exceção para nome nulo ou vazio
        assertThrows(IllegalArgumentException.class, () -> {
            new Produto(null, "Descrição válida", 10.0, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Produto("", "Descrição válida", 10.0, 5);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Produto("   ", "Descrição válida", 10.0, 5); // Testando só espaços
        });
    }

    @Test
    void produto_nao_deve_ter_descricao_vazia() {
        // ACT & ASSERT: Verifica se o construtor lança a exceção para descrição nula ou vazia
        assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Nome válido", null, 10.0, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Nome válido", "   ", 10.0, 5);
        });
    }

    @Test
    void produto_nao_deve_ter_preco_negativo_ou_zero() {
        // ACT & ASSERT: Verifica se o construtor lança a exceção para preço <= 0
        assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Nome", "Desc", -10.0, 5);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Nome", "Desc", 0.0, 5);
        });
    }

    @Test
    void produto_nao_deve_ter_estoque_negativo() {
        // ACT & ASSERT: Verifica se o construtor lança a exceção para estoque negativo
        assertThrows(IllegalArgumentException.class, () -> {
            new Produto("Nome", "Desc", 10.0, -1);
        });
    }
    
    // Teste adicional: garante que o setter também aplica a validação
    @Test
    void setEstoque_deveLancarExcecao_quandoValorNegativo() {
        // ARRANGE
        Produto produto = new Produto("Item", "Desc", 10.0, 5);
        
        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            produto.setEstoque(-10);
        });
        
        // ASSERT: Garante que o valor não foi alterado
        assertEquals(5, produto.getEstoque());
    }

    @Test
    void darBaixaEstoque_deveLancarExcecao_quandoEstoqueInsuficiente(){
        Produto produto = new Produto("Item", "Desc", 10.0, 5);

        assertThrows(IllegalArgumentException.class, () -> {
            produto.darBaixaEstoque(10);
        });

        assertEquals(5, produto.getEstoque());
    }

}