package com.ada.t1420.supermercado.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ItemCompraTest {

    @Test
    void deveCalcularSubtotalCorretamente() {
        // Entrada: Produto R$ 10.00, Quantidade 3
        
        // Setup de um produto simulado (assumindo que já passou no ProdutoTest)
        Produto produto = new Produto("PRD1", "Arroz 5kg", new BigDecimal("10.00"));
        int quantidade = 3;
        
        // Resultado Esperado: Subtotal deve ser R$ 30.00 (10.00 * 3)
        BigDecimal subtotalEsperado = new BigDecimal("30.00");
        
        // Ação: Criar o ItemCompra
        ItemCompra item = new ItemCompra(produto, quantidade);
        
        
        // Verificação
        assertEquals(0, subtotalEsperado.compareTo(item.getSubtotal()), 
                     "O subtotal deve ser 30.00 para 3 unidades de 10.00.");
    }
    
    // Cenário: Validação de Quantidade Zero ou Negativa (Requisito implícito em "quantidade maior que zero")
    @Test
    void deveLancarExcecaoAoCriarComQuantidadeNaoPositiva() {
        // Setup de um produto simulado
        Produto produto = new Produto("PRD2", "Feijão 1kg", new BigDecimal("5.00"));

        // Ação/Resultado Esperado: Tentar criar ItemCompra com quantidade 0
        assertThrows(IllegalArgumentException.class, () -> {
            new ItemCompra(produto, 0);
        });

        // Ação/Resultado Esperado: Tentar criar ItemCompra com quantidade -1
        assertThrows(IllegalArgumentException.class, () -> {
            new ItemCompra(produto, -1);
        });
    }
}