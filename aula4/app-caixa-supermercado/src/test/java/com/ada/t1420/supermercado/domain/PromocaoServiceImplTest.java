package com.ada.t1420.supermercado.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Testes de Unidade para PromocaoServiceImpl, usando o modelo ConfiguracaoDesconto (Percentual).
 * Localização esperada: src/test/java/com/caixa/service/PromocaoServiceImplTest.java
 */
public class PromocaoServiceImplTest {

    // Constantes
    private static final String SKU_BANANA = "B1";
    private static final int SCALE = 2;
    private static final LocalDate HOJE = LocalDate.now();

    // Mocks e Injeções
    @Mock
    private PromocaoRepository repository; // Mock: Simula o acesso às configurações
    
    @InjectMocks
    private PromocaoServiceImpl service; // SUT (System Under Test): A classe concreta

    // Definição da regra de desconto: 10% OFF em Banana se comprar 5 ou mais
    private ConfiguracaoDesconto configBanana10Off;

    @BeforeEach
    void setUp() {
        // 1. Inicializa os mocks
        MockitoAnnotations.openMocks(this);
        
        // 2. Cria a configuração promocional que será usada nos testes
        // Desconto de 10% (0.10), Mínimo de 5 unidades, Vigente hoje.
        configBanana10Off = new ConfiguracaoDesconto(
            SKU_BANANA, 
            HOJE.minusDays(1), // Início: Ontem (Vigente)
            HOJE.plusDays(7),  // Fim: Daqui a 7 dias (Vigente)
            5,                 // Quantidade Mínima: 5
            new BigDecimal("0.10") // 10% de desconto
        );
        
        // 3. Configura o Mock para retornar APENAS a configuração de desconto de banana
        // Assumimos que o PromocaoRepository foi atualizado para retornar ConfiguracaoDesconto
        when(repository.buscarRegrasAtivas()).thenReturn(List.of(configBanana10Off));
    }
    
    
    // --- Testes de Cenário ---
    
    /**
     * Cenário: Desconto Percentual com Quantidade Mínima Atingida.
     * Produto: Banana (R$ 2.00). Config: 10% OFF, Mínimo 5.
     * Compra: 5 Bananas.
     * Desconto esperado: 5 * R$ 2.00 = R$ 10.00 (Subtotal). 10% de R$ 10.00 = R$ 1.00.
     */
    @Test
    void deveAplicar10PorcentoDescontoQuandoQuantidadeMinimaAtingida() {
        // ARRANGE
        Produto banana = new Produto(SKU_BANANA, "Banana Prata", new BigDecimal("2.00"));
        ItemCompra item = new ItemCompra(banana, 5); // 5 unidades
        List<ItemCompra> itens = List.of(item);

        BigDecimal descontoEsperado = new BigDecimal("1.00").setScale(SCALE, RoundingMode.HALF_UP); // 10% de 10.00

        // ACT
        BigDecimal descontoCalculado = service.aplicarPromocoes(itens);

        // ASSERT (🔴 RED: Este teste falhará inicialmente porque a lógica do PromocaoServiceImpl
        // ainda precisa ser atualizada para usar ConfiguracaoDesconto e calcular o percentual)
        assertEquals(descontoEsperado, descontoCalculado, "O desconto deve ser 10% do subtotal (R$ 1.00).");
    }

    /**
     * Cenário: Quantidade Mínima NÃO Atingida.
     * Produto: Banana (R$ 2.00). Config: 10% OFF, Mínimo 5.
     * Compra: 4 Bananas.
     * Desconto esperado: R$ 0.00.
     */
    @Test
    void deveRetornarZeroDescontoQuandoQuantidadeMinimaNaoAtingida() {
        // ARRANGE
        Produto banana = new Produto(SKU_BANANA, "Banana Prata", new BigDecimal("2.00"));
        ItemCompra item = new ItemCompra(banana, 4); // 4 unidades
        List<ItemCompra> itens = List.of(item);

        BigDecimal descontoEsperado = BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);

        // ACT
        BigDecimal descontoCalculado = service.aplicarPromocoes(itens);

        // ASSERT (🔴 RED: Este teste falhará inicialmente)
        assertEquals(descontoEsperado, descontoCalculado, "O desconto deve ser zero se a quantidade for menor que a mínima (5).");
    }
}