# 📝 Cenários de Teste Enriquecidos com Entradas e Saídas Esperadas

Abaixo estão os cenários de teste detalhados para as funcionalidades do sistema de estacionamento.

---

## Emite Ticket 🚗

| Caso | Descrição do Cenário | Entrada (Input) | Resultado Esperado (Output) |
| :--- | :--- | :--- | :--- |
| **Case 1** | **Deve emitir um ticket com ID, data e hora atual e placa do veículo.** | Veículo com **Placa: ABC1234**, Tipo: Carro. Hora de Chegada: **2025-11-25 10:00:00**. | Novo Ticket gerado. Exemplo: `{ ID: 12345, Placa: ABC1234, Entrada: 2025-11-25 10:00:00, Estacionamento_ID: 01, Status: PENDENTE, Tolerancia: 15 min }` |
| **Case 2** | **Deve retornar um ticket do banco de dados caso o ticket ainda não conste 'deu saída'.** | Veículo com **Placa: XYZ9876** tenta entrar. Um ticket ativo para essa placa existe no BD: `{ ID: 54321, Placa: XYZ9876, Status: PENDENTE }`. | O sistema **não emite** um novo ticket. Retorna o Ticket existente: `{ ID: 54321, Placa: XYZ9876, Status: PENDENTE }`. |

---

## Isenção de Ticket 🆓

| Caso | Descrição do Cenário | Entrada (Input) | Resultado Esperado (Output) |
| :--- | :--- | :--- | :--- |
| **Case 1** | **Deve considerar isento se permanência menor que 15 minutos.** | Ticket `{ ID: 101, Entrada: 11:00 }`. Tentativa de saída às **11:14:59**. | Status do Ticket alterado para **ISENTO**. Saída permitida. |
| **Case 2** | **Deve pagar se permanência maior que 15 minutos.** | Ticket `{ ID: 102, Entrada: 11:00 }`. Tentativa de saída às **11:15:01**. | Status do Ticket permanece **PENDENTE**. Valor de cobrança deve ser calculado. |
| **Case 3** | **Deve considerar isento se comprovante de compra.** | Ticket `{ ID: 103, Status: PENDENTE }` + Apresentação de **Comprovante de Compra Válido** (dentro do prazo e loja). | Status do Ticket alterado para **ISENTO**. Saída permitida. |
| **Case 4** | **Deve considerar isento se placa cadastrada de funcionário.** | Ticket `{ ID: 104, Placa: FUN0001 }`. A Placa **FUN0001** está na lista de funcionários. | Status do Ticket alterado para **ISENTO_FUNCIONARIO**. Saída permitida. |
| **Case 5** | **Deve considerar isento se apresentado cartão federal de estacionamento gratuito.** | Ticket `{ ID: 105, Status: PENDENTE }` + Apresentação de **Cartão Federal de Estacionamento Gratuito** válido. | Status do Ticket alterado para **ISENTO**. Saída permitida. |
| **Case 6** | **Deve pagar se exceder limite de tempo após validação por pagamento em 30 minutos.** | Ticket `{ ID: 106, Status: PAGO, Data_Hora_Pagamento: 14:00 }`. Tentativa de saída às **14:30:01**. | Status do Ticket retorna para **PENDENTE**. Nova cobrança/Pagamento exigido para a saída. |
| **Case 7** | **Deve ser isento se comprovar compra em loja no período de 60 minutos (da entrada).** | Ticket `{ ID: 107, Entrada: 15:00 }`. Apresentação de Comprovante de Compra com **Data/Hora: 15:55:00**. | Isenção concedida. Status do Ticket alterado para **ISENTO**. |
| **Case 8** | **Deve pagar se comprovante compra em loja expirou em mais de 60 minutos (da entrada).** | Ticket `{ ID: 108, Entrada: 15:00 }`. Apresentação de Comprovante de Compra com **Data/Hora: 16:05:00**. | Isenção **negada**. Status do Ticket permanece **PENDENTE**. Valor de cobrança calculado. |

---

## Pagamento 💳

| Caso | Descrição do Cenário | Entrada (Input) | Resultado Esperado (Output) |
| :--- | :--- | :--- | :--- |
| **Case 1** | **Deve integrar com sistema de pagamento e marcar ticket como pago com hora e data de pagamento.** | Ticket `{ ID: 201, Status: PENDENTE }`. Sistema de Pagamento retorna **Sucesso**. Hora Atual: **16:30:00**. | Status do Ticket alterado para **PAGO**. Campo `Data_Hora_de_Pagamento` preenchido com **16:30:00**. |
| **Case 2** | **Não deve alterar para pago o status do ticket se pagamento falhar.** | Ticket `{ ID: 202, Status: PENDENTE }`. Sistema de Pagamento retorna **Falha/Recusa**. | Status do Ticket permanece **PENDENTE**. `Data_Hora_de_Pagamento` permanece vazio/nulo. |

---

## Dar Saída 🚪

| Caso | Descrição do Cenário | Entrada (Input) | Resultado Esperado (Output) |
| :--- | :--- | :--- | :--- |
| **Case 1** | **Deve alterar o status para FINALIZADO se status for ISENTO.** | Ticket `{ ID: 301, Status: ISENTO }`. Tentativa de saída. | Status do Ticket alterado para **FINALIZADO**. Saída liberada. |
| **Case 2** | **Deve alterar o status para FINALIZADO se status for PAGO e DATA HORA DE PAGAMENTO em até 15 minutos da data hora atual.** | Ticket `{ ID: 302, Status: PAGO, Data_Hora_Pagamento: 17:00:00 }`. Tentativa de saída às **17:14:59**. | Status do Ticket alterado para **FINALIZADO**. Saída liberada. |
| **Case 3** | **Não deve alterar o status para FINALIZADO se hora atual for 22h (se modo 24h desativado), independentemente do status do ticket.** | Ticket `{ ID: 303, Status: PAGO }`. Tentativa de saída às **22:01:00** (assumindo fechamento às 22h). | Status do Ticket **não** é alterado para FINALIZADO. Saída **bloqueada** (requer intervenção/ação para saída fora do horário). |
| **Case 4** | **Deve alterar o status para FINALIZADO se status for ISENTO FUNCIONARIO.** | Ticket `{ ID: 304, Status: ISENTO_FUNCIONARIO }`. Tentativa de saída. | Status do Ticket alterado para **FINALIZADO**. Saída liberada. |
| **Case 5** | **Ao dar saída o ticket não precisa mais estar registrado no sistema (ativo).** | Ticket `{ ID: 305, Status: FINALIZADO }`. | O Ticket é **removido** da lista de tickets ativos ou marcado como **inativo** no banco de dados. |

---

## Validação do Comprovante 🧾

| Caso | Descrição do Cenário | Entrada (Input) | Resultado Esperado (Output) |
| :--- | :--- | :--- | :--- |
| **Case 1** | **Comprovante é válido se emitido por loja reconhecida, se data de validade dentro de 2h.** | Comprovante ID: C100. Loja: "Magazine X" (Reconhecida). Data/Hora Emissão: **10:00:00**. Hora de Validação: **11:59:59**. | Retorna **Comprovante Válido**. |
| **Case 2** | **Comprovante é válido se não foi usado em isenção anterior.** | Comprovante ID: C101. **Já foi usado** para isentar o Ticket ID: 900. Tentativa de usar para Ticket ID: 901. | Retorna **Comprovante Inválido** (Motivo: Já utilizado). |

---

## Cálculo do Preço 💲

*Assumindo a tabela de preços de exemplo:*
* *Carro (Tarifa Base - 1ª e 2ª hora): R\$ 10,00/h*
* *Carro (Hora Adicional - a partir da 3ª): R\$ 5,00/h*
* *Carro (Diária Máxima > 8h): R\$ 50,00*
* *Moto (Tarifa Base - 1ª e 2ª hora): R\$ 5,00/h*
* *Moto (Hora Adicional - a partir da 3ª): R\$ 2,50/h*

| Caso | Descrição do Cenário | Entrada (Input) | Regra/Cálculo/Resultado Esperado (Output) |
| :--- | :--- | :--- | :--- |
| **Case 1** | **As duas primeiras horas têm o mesmo valor.** | Permanência: **1h 30min** (Arredondado para 2h). Tipo: Carro. | Cálculo: 2h * R$ 10,00 = **R$ 20,00**. |
| **Case 2** | **Após duas primeiras horas, se paga valor por hora adicional.** | Permanência: **3h 15min** (Arredondado para 4h). Tipo: Carro. | Cálculo: 2h * R$ 10,00 + 2h * R$ 5,00 = R$ 20,00 + R$ 10,00 = **R$ 30,00**. |
| **Case 3** | **Se período maior que 8 horas, preço é de diária.** | Permanência: **10h 00min**. Tipo: Carro. | Preço deve ser limitado ao valor da diária: **R$ 50,00**. |
| **Case 4** | **As tabelas de preço variam por tipo de veículo.** | Permanência: **2h 01min** (Arredondado para 3h). Tipo: Moto. | Cálculo: 2h * R$ 5,00 + 1h * R$ 2,50 = R$ 10,00 + R$ 2,50 = **R$ 12,50**. |
| **Case 5** | **O cálculo do período é em hora com arredondamento para cima.** | Permanência: **4h 01min**. Tipo: Carro. | Período de cobrança: **5 horas**. Cálculo: 2h * R$ 10,00 + 3h * R$ 5,00 = R$ 20,00 + R$ 15,00 = **R$ 35,00**. |