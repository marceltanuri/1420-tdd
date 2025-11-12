package com.ada.t1420.supermercado.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável por aplicar as promoções aos itens de uma compra,
 * baseado no modelo de Configuração de Desconto Percentual.
 */
public class PromocaoServiceImpl implements PromocaoService {

    private final PromocaoRepository repository;
    private static final int SCALE = 2;

    public PromocaoServiceImpl(PromocaoRepository repository) {
        this.repository = repository;
    }

    @Override
    public BigDecimal aplicarPromocoes(List<ItemCompra> itens) {
        List<ConfiguracaoDesconto> configsAtivas = repository.buscarRegrasAtivas(); 
        
        LocalDate dataAtual = LocalDate.now(); 
        
        BigDecimal descontoTotal = BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        
        for (ItemCompra item : itens) {
            
            Optional<ConfiguracaoDesconto> configOpcional = configsAtivas.stream()
                .filter(config -> config.getSkuProduto().equals(item.getProduto().getCodigo()))
                .filter(config -> config.estaVigente(dataAtual))
                .findFirst();

            if (configOpcional.isPresent()) {
                ConfiguracaoDesconto config = configOpcional.get();
                
                // 2. Verifica se a condição de quantidade mínima foi atingida
                if (item.getQuantidade() >= config.getQuantidadeMinima()) {
                    
                    // 3. Calcula o subtotal do item (Quantidade * Preço Unitário)
                    // Este é o valor sobre o qual o desconto será aplicado.
                    BigDecimal subtotalItem = item.getProduto().getPrecoUnitario()
                        .multiply(new BigDecimal(item.getQuantidade()));
                        
                    // 4. Calcula o valor do desconto: Subtotal * Percentual
                    // Ex: 10.00 * 0.10 = 1.00
                    BigDecimal descontoItem = subtotalItem
                        .multiply(config.getPercentualDesconto())
                        .setScale(SCALE, RoundingMode.HALF_UP);

                    // 5. Acumula o desconto total
                    descontoTotal = descontoTotal.add(descontoItem);
                }
            }
        }

        return descontoTotal;
    }
    
}