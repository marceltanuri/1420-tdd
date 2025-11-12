package com.ada.t1420.supermercado.domain;


import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {

    // Cenário: Validação de Preço Negativo
    @Test
    void deveLancarExcecaoAoCriarComPrecoNegativo() {

        // A A A
        // A: Arrange (configurar)
        // A: Act (executar)
        // A: Assert (verificar)


        // Ação: Tentar criar um produto com preço -0.01
        BigDecimal precoNegativo = new BigDecimal("-0.01");
        
        // Resultado Esperado: Deve lançar IllegalArgumentException
        // Execução e também a validação
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Produto("P1", "Refrigerante", precoNegativo);
        });

        // Verificação adicional (opcional): A mensagem deve ser clara
        assertTrue(exception.getMessage().contains("Preço unitário deve ser estritamente positivo"));
    }

    // Cenário: Validação de Preço Zero
    @Test
    void deveLancarExcecaoAoCriarComPrecoZero() {
        // Ação: Tentar criar um produto com preço 0.00
        BigDecimal precoZero = BigDecimal.ZERO;

        // Resultado Esperado: Deve lançar IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Produto("P2", "Biscoito", precoZero);
        });

        // Verificação adicional (opcional): A mensagem deve ser clara
        assertTrue(exception.getMessage().contains("Preço unitário deve ser estritamente positivo"));
    }
    
    // Cenário de sucesso (caminho feliz)
    @Test
    void deveCriarProdutoComPrecoPositivo() {
        // Ação: Criar um produto com preço válido
        BigDecimal precoValido = new BigDecimal("1.50");
        
        // Resultado Esperado: Não deve lançar exceção e o objeto deve ser válido
        assertDoesNotThrow(() -> {
            Produto produto = new Produto("P3", "Pão Francês", precoValido);
            assertNotNull(produto);
        });
    }
}