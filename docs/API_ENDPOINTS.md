# Documentação dos Endpoints — Dashboard API

Base URL: `/api/dashboard`

> **Autenticação:** Todos os endpoints exigem usuário autenticado com perfil `ADMIN` ou `GERENTE`. Caso contrário, retorna `403 Forbidden`.

---

## 1. Indicadores Gerais de Vendas

**GET** `/api/dashboard/indicadores`

Retorna os indicadores consolidados de vendas.

### Parâmetros de query (opcionais)

| Parâmetro   | Tipo   | Formato      | Descrição          |
|-------------|--------|--------------|--------------------|
| dataInicio  | string | `YYYY-MM-DD` | Filtro data início |
| dataFim     | string | `YYYY-MM-DD` | Filtro data fim    |

### Exemplo de requisição

```
GET /api/dashboard/indicadores?dataInicio=2025-01-01&dataFim=2025-01-31
```

### Exemplo de resposta — `200 OK`

```json
{
  "totalPedidos": 150,
  "pedidosFinalizados": 120,
  "pedidosCancelados": 10,
  "pedidosEmAndamento": 20,
  "faturamentoTotal": 8500.50,
  "ticketMedio": 70.84,
  "taxaCancelamento": 6.67
}
```

### Possíveis erros

| Código | Descrição |
|--------|-----------|
| 401    | Não autenticado |
| 403    | Perfil sem permissão (não é ADMIN nem GERENTE) |

---

## 2. Vendas por Período

**GET** `/api/dashboard/vendas-por-periodo`

Retorna o faturamento e a quantidade de pedidos finalizados agrupados por dia.

### Parâmetros de query (opcionais)

| Parâmetro   | Tipo   | Formato      | Descrição          |
|-------------|--------|--------------|--------------------|
| dataInicio  | string | `YYYY-MM-DD` | Filtro data início |
| dataFim     | string | `YYYY-MM-DD` | Filtro data fim    |

### Exemplo de requisição

```
GET /api/dashboard/vendas-por-periodo?dataInicio=2025-01-01&dataFim=2025-01-07
```

### Exemplo de resposta — `200 OK`

```json
[
  { "periodo": "01/01/2025", "quantidadePedidos": 12, "valorTotal": 850.00 },
  { "periodo": "02/01/2025", "quantidadePedidos": 8, "valorTotal": 560.50 },
  { "periodo": "03/01/2025", "quantidadePedidos": 15, "valorTotal": 1020.00 }
]
```

### Possíveis erros

| Código | Descrição |
|--------|-----------|
| 401    | Não autenticado |
| 403    | Perfil sem permissão |

---

## 3. Situação dos Pedidos

**GET** `/api/dashboard/situacao-pedidos`

Retorna a contagem de pedidos agrupados por status.

### Parâmetros de query (opcionais)

| Parâmetro   | Tipo   | Formato      | Descrição          |
|-------------|--------|--------------|--------------------|
| dataInicio  | string | `YYYY-MM-DD` | Filtro data início |
| dataFim     | string | `YYYY-MM-DD` | Filtro data fim    |

### Exemplo de requisição

```
GET /api/dashboard/situacao-pedidos
```

### Exemplo de resposta — `200 OK`

```json
[
  { "status": "RECEBIDO", "quantidade": 5 },
  { "status": "EM_PREPARACAO", "quantidade": 3 },
  { "status": "PRONTO", "quantidade": 2 },
  { "status": "SAIU_PARA_ENTREGA", "quantidade": 1 },
  { "status": "RETIRADO", "quantidade": 0 },
  { "status": "FINALIZADO", "quantidade": 120 },
  { "status": "CANCELADO", "quantidade": 10 }
]
```

### Possíveis erros

| Código | Descrição |
|--------|-----------|
| 401    | Não autenticado |
| 403    | Perfil sem permissão |

---

## 4. Produtos Mais Vendidos

**GET** `/api/dashboard/produtos-mais-vendidos`

Retorna o ranking de produtos por quantidade vendida (excluindo pedidos cancelados).

### Parâmetros de query (opcionais)

| Parâmetro   | Tipo    | Padrão | Descrição                     |
|-------------|---------|--------|-------------------------------|
| dataInicio  | string  | —      | Filtro data início            |
| dataFim     | string  | —      | Filtro data fim               |
| limite      | integer | 10     | Quantidade máxima de produtos |

### Exemplo de requisição

```
GET /api/dashboard/produtos-mais-vendidos?limite=5
```

### Exemplo de resposta — `200 OK`

```json
[
  { "produtoId": "abc123", "nome": "Pizza Calabresa Grande", "quantidadeVendida": 45, "faturamento": 2250.00 },
  { "produtoId": "def456", "nome": "Esfiha de Carne", "quantidadeVendida": 38, "faturamento": 380.00 },
  { "produtoId": "ghi789", "nome": "Refrigerante 2L", "quantidadeVendida": 30, "faturamento": 300.00 }
]
```

### Possíveis erros

| Código | Descrição |
|--------|-----------|
| 401    | Não autenticado |
| 403    | Perfil sem permissão |

---

## 5. Vendas por Tipo de Produto

**GET** `/api/dashboard/vendas-por-tipo`

Retorna o faturamento e a quantidade vendida agrupados por tipo de produto (Pizza, Esfiha, Bebida, Lanche, etc).

### Parâmetros de query (opcionais)

| Parâmetro   | Tipo   | Formato      | Descrição          |
|-------------|--------|--------------|--------------------|
| dataInicio  | string | `YYYY-MM-DD` | Filtro data início |
| dataFim     | string | `YYYY-MM-DD` | Filtro data fim    |

### Exemplo de requisição

```
GET /api/dashboard/vendas-por-tipo?dataInicio=2025-01-01&dataFim=2025-12-31
```

### Exemplo de resposta — `200 OK`

```json
[
  { "tipo": "Pizza", "quantidadeVendida": 200, "faturamento": 10000.00 },
  { "tipo": "Esfiha", "quantidadeVendida": 150, "faturamento": 1500.00 },
  { "tipo": "Bebida", "quantidadeVendida": 100, "faturamento": 800.00 },
  { "tipo": "Lanche", "quantidadeVendida": 50, "faturamento": 750.00 }
]
```

### Possíveis erros

| Código | Descrição |
|--------|-----------|
| 401    | Não autenticado |
| 403    | Perfil sem permissão |
