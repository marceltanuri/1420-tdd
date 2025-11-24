# 🚀 Desafio de Testes Automatizados: SGE - Sistema de Gerenciamento de Estacionamento

## 🎯 Objetivo

Desenvolver um **Sistema de Gerenciamento de Estacionamento (SGE)** funcional e garantir sua qualidade através de uma **cobertura robusta de testes automatizados**.

O foco não é apenas na implementação, mas na **estratégia de teste** escolhida para lidar com uma **dependência externa crítica**: a validação da isenção de pagamento.

---

## 🏗️ Requisitos Funcionais

O SGE deve ser capaz de gerenciar o fluxo de veículos e pagamentos:

### 1. Fluxo de Operação Básica

* **Entrada de Veículo:** Um registro de entrada deve ser criado, capturando a placa do veículo e o carimbo de tempo (timestamp) de entrada.
* **Saída de Veículo e Cálculo:** Ao registrar a saída, o sistema deve calcular o tempo total de permanência.
* **Cálculo da Tarifa:** Implementar uma regra simples de tarifa (exemplo: R\$ 5,00 a primeira hora, R\$ 3,00 a cada hora subsequente, com cobrança fracionada por minuto).
* **Processamento de Pagamento:** Simular o processamento do valor total (o sistema deve registrar o pagamento como `Concluído`).

### 2. O Desafio Central: Isenção de Pagamento

O sistema deve permitir que o motorista solicite a isenção do pagamento, caso apresente uma **Nota Fiscal Eletrônica (NF-e)** de um estabelecimento credenciado.

* A NF-e será fornecida ao SGE de alguma forma (vocês devem definir a **interface** e o **formato** da informação).
* O sistema deve **validar** esta NF-e. Se a validação for bem-sucedida, o valor a pagar deve ser zerado (R\$ 0,00).

---

## ⚔️ O Desafio de Testabilidade: A Dependência Externa

A validação da NF-e é o ponto de maior complexidade. A informação de que a NF-e é válida e isenta o pagamento reside em um **Serviço de Validação Externo**.

**Sua missão é escolher e justificar a melhor estratégia de integração**

* **Testes Unitários:** Cobrir a lógica de cálculo de tarifa e as funções de validação interna.
* **Testes de Integração:** Garantir a persistência de dados (banco de dados) e, o mais importante, **a interação com o módulo de validação de isenção**.
* **Testes E2E (End-to-End):** Simular o fluxo completo: Entrada -> Validação (Sucesso/Falha) -> Saída/Pagamento.

---

### Histórias de Usuário a Testar

Desenvolver e rodar testes que cubram os seguintes cenários críticos:

| Cenário de Teste | Descrição |
| :--- | :--- |
| **Cálculo Base** | Veículo permanece por 2 horas e 30 minutos. O cálculo da tarifa deve estar correto. |
| **Isenção Válida** | O veículo apresenta um comprovante de isenção **válido** (Token ou Resposta API). O valor a pagar deve ser R\$ 0,00. |
| **Comprovante Expirado** | O comprovante/NF-e está em um formato correto, mas **fora da validade** (ex: expirou após 24h). O valor a pagar deve ser o valor total. |
| **Comprovante Inválido** | O comprovante está com o **formato incorreto** ou com a **assinatura inválida**. O sistema deve rejeitar a isenção e cobrar o valor total. |
| **Pagamento Completo** | O veículo não tem isenção e o pagamento ocorre com sucesso. |

---

Boa sorte! Que vença a solução mais testável e robusta!