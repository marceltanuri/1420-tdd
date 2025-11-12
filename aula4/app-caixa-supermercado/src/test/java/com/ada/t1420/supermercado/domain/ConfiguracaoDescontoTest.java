package com.ada.t1420.supermercado.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes de unidade para o método de verificação de vigência da Configuração de Desconto.
 */
public class ConfiguracaoDescontoTest {

    private static final String SKU_TESTE = "T1";
    private static final BigDecimal DESCONTO_TESTE = new BigDecimal("0.05");

    /**
     * Cenário: A data de hoje está dentro do período de vigência.
     */
    @Test
    void deveRetornarTrueQuandoDataAtualEstiverDentroDaVigencia() {
        // ARRANGE
        LocalDate dataInicio = LocalDate.now().minusDays(5); // 5 dias atrás
        LocalDate dataFim = LocalDate.now().plusDays(5);    // 5 dias à frente
        LocalDate dataAtual = LocalDate.now();              // Hoje

        ConfiguracaoDesconto config = new ConfiguracaoDesconto(
            SKU_TESTE, dataInicio, dataFim, 1, DESCONTO_TESTE
        );

        // ACT & ASSERT
        // Deve ser vigente
        assertTrue(config.estaVigente(dataAtual), "A configuração deve ser vigente na data atual.");
    }

    /**
     * Cenário: A data de hoje é o dia inicial da vigência (limite inferior inclusivo).
     */
    @Test
    void deveRetornarTrueQuandoDataAtualForIgualADataInicio() {
        // ARRANGE
        LocalDate dataInicio = LocalDate.now(); 
        LocalDate dataFim = LocalDate.now().plusDays(5);
        LocalDate dataAtual = LocalDate.now(); 

        ConfiguracaoDesconto config = new ConfiguracaoDesconto(
            SKU_TESTE, dataInicio, dataFim, 1, DESCONTO_TESTE
        );

        // ACT & ASSERT
        assertTrue(config.estaVigente(dataAtual), "A configuração deve ser vigente se for a data de início.");
    }

    /**
     * Cenário: A data de hoje é o dia final da vigência (limite superior inclusivo).
     */
    @Test
    void deveRetornarTrueQuandoDataAtualForIgualADataFim() {
        // ARRANGE
        LocalDate dataInicio = LocalDate.now().minusDays(5); 
        LocalDate dataFim = LocalDate.now();                // Hoje
        LocalDate dataAtual = LocalDate.now(); 

        ConfiguracaoDesconto config = new ConfiguracaoDesconto(
            SKU_TESTE, dataInicio, dataFim, 1, DESCONTO_TESTE
        );

        // ACT & ASSERT
        assertTrue(config.estaVigente(dataAtual), "A configuração deve ser vigente se for a data de fim.");
    }

    /**
     * Cenário: A data de hoje está antes do período de vigência.
     */
    @Test
    void deveRetornarFalseQuandoDataAtualForAnteriorADataInicio() {
        // ARRANGE
        LocalDate dataInicio = LocalDate.now().plusDays(1); // Começa amanhã
        LocalDate dataFim = LocalDate.now().plusDays(5);
        LocalDate dataAtual = LocalDate.now();             // Hoje

        ConfiguracaoDesconto config = new ConfiguracaoDesconto(
            SKU_TESTE, dataInicio, dataFim, 1, DESCONTO_TESTE
        );

        // ACT & ASSERT
        assertFalse(config.estaVigente(dataAtual), "A configuração não deve ser vigente se for anterior à data de início.");
    }

    /**
     * Cenário: A data de hoje está após o período de vigência.
     */
    @Test
    void deveRetornarFalseQuandoDataAtualForPosteriorADataFim() {
        // ARRANGE
        LocalDate dataInicio = LocalDate.now().minusDays(5);
        LocalDate dataFim = LocalDate.now().minusDays(1);   // Terminou ontem
        LocalDate dataAtual = LocalDate.now();              // Hoje

        ConfiguracaoDesconto config = new ConfiguracaoDesconto(
            SKU_TESTE, dataInicio, dataFim, 1, DESCONTO_TESTE
        );

        // ACT & ASSERT
        assertFalse(config.estaVigente(dataAtual), "A configuração não deve ser vigente se for posterior à data de fim.");
    }
}