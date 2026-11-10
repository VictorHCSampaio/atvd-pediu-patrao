# Evidências de Testes — Dashboard Administrativo

## 1. Testes dos Endpoints da API (via curl/Postman)

### 1.1 GET /api/dashboard/indicadores — Sem filtro

**Requisição:**
```
GET http://localhost:8080/api/dashboard/indicadores
Authorization: (sessão autenticada como gerente)
```

**Resposta esperada:** `200 OK`
```json
{
  "totalPedidos": 25,
  "pedidosFinalizados": 18,
  "pedidosCancelados": 3,
  "pedidosEmAndamento": 4,
  "faturamentoTotal": 1350.00,
  "ticketMedio": 75.00,
  "taxaCancelamento": 12.0
}
```

**Resultado:** ✅ Indicadores retornados corretamente com todos os campos preenchidos.

---

### 1.2 GET /api/dashboard/indicadores — Com filtro de datas

**Requisição:**
```
GET http://localhost:8080/api/dashboard/indicadores?dataInicio=2025-05-01&dataFim=2025-05-31
```

**Resultado:** ✅ Retorna somente pedidos do mês de maio. Valores de faturamento e contadores filtrados.

---

### 1.3 GET /api/dashboard/vendas-por-periodo

**Requisição:**
```
GET http://localhost:8080/api/dashboard/vendas-por-periodo
```

**Resultado:** ✅ Retorna array agrupado por dia com `periodo`, `quantidadePedidos` e `valorTotal`. Dias sem pedidos finalizados não aparecem.

---

### 1.4 GET /api/dashboard/situacao-pedidos

**Requisição:**
```
GET http://localhost:8080/api/dashboard/situacao-pedidos
```

**Resultado:** ✅ Retorna todos os 7 status possíveis, mesmo aqueles com quantidade 0, facilitando a montagem do gráfico.

---

### 1.5 GET /api/dashboard/produtos-mais-vendidos

**Requisição:**
```
GET http://localhost:8080/api/dashboard/produtos-mais-vendidos?limite=5
```

**Resultado:** ✅ Retorna os 5 produtos com maior quantidade vendida em ordem decrescente. Pedidos cancelados são excluídos do cálculo.

---

### 1.6 GET /api/dashboard/vendas-por-tipo

**Requisição:**
```
GET http://localhost:8080/api/dashboard/vendas-por-tipo
```

**Resultado:** ✅ Agrupa por tipo do produto (Pizza, Esfiha, Bebida, Lanche). Produtos sem tipo aparecem como "Outros".

---

## 2. Testes de Segurança

### 2.1 Acesso sem autenticação

**Requisição:**
```
GET http://localhost:8080/api/dashboard/indicadores
(sem sessão/cookie)
```

**Resultado:** ✅ Retorna `302 Redirect` para `/login` (comportamento padrão do Spring Security com formulário).

---

### 2.2 Acesso com perfil ATENDENTE

**Requisição:**
```
GET http://localhost:8080/api/dashboard/indicadores
Authorization: (sessão autenticada como atendente)
```

**Resultado:** ✅ Retorna `403 Forbidden`. Apenas ADMIN e GERENTE possuem acesso.

---

### 2.3 Acesso com perfil GERENTE

**Requisição:**
```
GET http://localhost:8080/api/dashboard/indicadores
Authorization: (sessão autenticada como gerente)
```

**Resultado:** ✅ Retorna `200 OK` com os dados do dashboard.

---

## 3. Testes de Frontend (Dashboard)

### 3.1 Carregamento inicial

- Abrir `/dashboard` logado como `gerente` (senha: `teste123`).
- **Resultado:** ✅ Cards de indicadores carregam com valores. Gráficos renderizam (barras, doughnut, pizza, horizontal).

### 3.2 Filtro por data

- Preencher Data Início e Data Fim e clicar em "Filtrar".
- **Resultado:** ✅ Todos os cards e gráficos atualizam sem recarregar a página.

### 3.3 Limpar filtros

- Clicar em "Limpar".
- **Resultado:** ✅ Campos de data limpos e dashboard volta ao estado sem filtro.

### 3.4 Dashboard sem dados

- Filtrar um período sem pedidos.
- **Resultado:** ✅ Cards mostram 0. Gráficos ficam vazios sem erro. Tabela mostra mensagem "Nenhum produto vendido no período."

### 3.5 Sidebar — Visibilidade do link

- Logar como `atendente`.
- **Resultado:** ✅ Link "Dashboard" não aparece na sidebar. Acessar `/dashboard` diretamente é bloqueado.

---

## 4. Testes de Tratamento de Dados (RF07)

### 4.1 Pedido sem data de entrada

- Pedido com `dataHoraEntrada = null` no banco.
- **Resultado:** ✅ Ignorado nos filtros por data, não causa `NullPointerException`.

### 4.2 Pedido sem itens

- Pedido com `itens = null`.
- **Resultado:** ✅ Não impacta ranking de produtos (o stream de flatMap filtra pedidos sem itens).

### 4.3 Produto sem tipo definido

- Item de pedido cujo produto tem `tipo = null`.
- **Resultado:** ✅ Agrupado como "Outros" no gráfico de vendas por tipo.
