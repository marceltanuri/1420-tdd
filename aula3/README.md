# 📝 Desafio TDD: Extração de Cenários para Sistema de Caixa

**Objetivo:** Analisar os requisitos do sistema de caixa de supermercado e aplicar a primeira fase do TDD: **RED** (Redigir o Teste Falhando). Para isso, vocês devem extrair e documentar os cenários de teste essenciais, separando-os por **Teste de Estado (Sem Mocks)** e **Teste de Comportamento (Com Mocks)**.

---

## 1. 🛒 Enunciado do Sistema de Caixa

Você é o desenvolvedor responsável pela lógica de *backend* de um novo sistema de caixa. Seu trabalho é construir um módulo de cálculo de compra (`CompraService`) que deve ser **preciso**, **robusto** e **altamente testável**.

### A. Regras de Domínio e Validação (Testes de Estado)

As classes de domínio não devem ter dependências externas e representam a lógica pura:

* **`Produto`**: Todo produto deve ser criado com um **preço unitário (`BigDecimal`) estritamente positivo**.
* **`ItemCompra`**: Um item na cesta deve ter uma **quantidade (`int`) maior que zero**. O item é responsável por calcular o seu próprio **subtotal** (`preço * quantidade`).
* **`PromocaoService`** (Lógica): Este serviço implementa a regra de desconto mais crítica: **"Pague 2, Leve 3"** para o produto **Leite (SKU "L1")**. O desconto deve ser calculado corretamente (1 unidade gratuita a cada 3 compradas do produto "L1").

### B. Fluxo de Orquestração (Testes de Comportamento)

O `CompraService` é o orquestrador e depende dos seguintes componentes (que serão injetados via interface):

* `PromocaoService`
* `EstacionamentoService`
* `ImpressaoFiscalService`

**Fluxo de Finalização de Compra:**

1. Calcular o subtotal bruto da lista de itens.
2. Chamar o `PromocaoService` para obter o desconto de promoções.
3. Chamar o `EstacionamentoService` para obter o desconto do ticket.
4. Calcular o **Valor Total da Compra** = Subtotal - Desc. Promoção - Desc. Estacionamento.
5. Chamar o `ImpressaoFiscalService` com o objeto `Compra` finalizado.

**Requisitos Adicionais do `CompraService`:**

* Se o `EstacionamentoService` lançar uma exceção (`TicketInvalidoException`), o `CompraService` **não deve aplicar o desconto de estacionamento**, mas **deve propagar a exceção** para alertar o operador.
* A impressão do cupom (`ImpressaoFiscalService`) **só pode ocorrer** se todo o cálculo for concluído com sucesso.

## 2. 📋 Tabela de Extração de Cenários (A Ser Preenchida)

Preencha as tabelas a seguir, identificando os cenários de teste (RED) para guiar o desenvolvimento.

### A. Teste de Estado (Lógica Pura - Sem Mocks)

| Classe | Cenário de Teste | Ação/Entrada | Resultado Esperado |
| :--- | :--- | :--- | :--- |
| **`Produto`** | Validação de Preço Negativo | Criar Produto com preço = -0.01 | Deve lançar `IllegalArgumentException`. |
| **`Produto`** | Validação de Preço Zero | Criar Produto com preço = 0.00 | Deve lançar `IllegalArgumentException`. |
| **`ItemCompra`** | Cálculo de Subtotal | Produto R$ 10.00, Quantidade 3 | Subtotal deve ser R$ 30.00. |
| **`PromocaoService`** | Pague 2, Leve 3 - Quantidade 3 | 3 unidades de Leite (R$ 5.00) | Desconto total deve ser R$ 5.00. |
| **`PromocaoService`** | Pague 2, Leve 3 - Quantidade 6 | 6 unidades de Leite (R$ 5.00) | Desconto total deve ser R$ 10.00. |
| **`PromocaoService`** | Pague 2, Leve 3 - Sem Desconto | Produto não promocional (5 unidades) | Desconto total deve ser R$ 0.00. |

---

### B. Teste de Comportamento (Orquestração - Com Mocks)

| Classe | Cenário de Teste | Configuração dos Mocks (`when()`) | Ação/Verificação Esperada |
| :--- | :--- | :--- | :--- |
| **`CompraService`** | Cálculo Total com Descontos | Promoção Mock retorna R$ 2.00, Estacionamento Mock retorna R$ 5.00. Subtotal real é R$ 50.00. | Valor Total da Compra deve ser R$ 43.00. |
| **`CompraService`** | Finalização bem-sucedida | Mocks retornam valores válidos. | **Verificar** (`verify()`) se `ImpressaoFiscalService.imprimirCupom()` foi chamado **exatamente 1 vez**. |
| **`CompraService`** | Ticket Inválido | Estacionamento Mock lança `TicketInvalidoException`. | **Verificar** (`assertThrows()`) se a exceção é propagada. |
| **`CompraService`** | Chamada Correta | Teste simples. | **Verificar** (`verify()`) se `PromocaoService.aplicarPromocoes()` foi chamado com a **lista correta de itens**. |