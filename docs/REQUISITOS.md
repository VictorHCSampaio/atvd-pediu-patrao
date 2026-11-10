# Pediu Patrão — Dashboard Administrativo

## Requisitos Funcionais

| Código | Descrição | Prioridade |
|--------|-----------|------------|
| RF01 | **Indicadores gerais de vendas** — O sistema exibe cards com: total de pedidos, faturamento total (pedidos finalizados), ticket médio, taxa de cancelamento, pedidos finalizados, em andamento e cancelados. | Alta |
| RF02 | **Análise de vendas (gráficos)** — Gráfico de barras com faturamento diário e linha sobreposta com quantidade de pedidos finalizados por dia. | Alta |
| RF03 | **Situação dos pedidos** — Gráfico do tipo doughnut mostrando a distribuição dos pedidos por status (RECEBIDO, EM_PREPARACAO, PRONTO, SAIU_PARA_ENTREGA, RETIRADO, FINALIZADO, CANCELADO). | Alta |
| RF04 | **Produtos mais vendidos** — Ranking horizontal dos 10 produtos com maior quantidade vendida e tabela detalhada com nome, quantidade e faturamento. Gráfico de pizza com vendas agrupadas por tipo de produto (Pizza, Esfiha, Bebida, Lanche). | Alta |
| RF05 | **Filtros de consulta** — Campos de data início e data fim que filtram todos os indicadores e gráficos simultaneamente. Botão "Limpar" retorna ao cenário sem filtro. | Média |
| RF06 | **Integração com a API** — O frontend consome os endpoints REST `/api/dashboard/*` via `fetch` assíncrono, sem recarregar a página. | Alta |
| RF07 | **Atualização e tratamento de dados** — Dados nulos ou ausentes são tratados com valores padrão (zero). Erros de requisição são capturados e logados no console sem travar a interface. | Média |

## Requisitos Não Funcionais

| Código | Descrição |
|--------|-----------|
| RNF01 | **Desempenho** — Os endpoints do dashboard devem responder em menos de 2 segundos para bases de até 10.000 pedidos. |
| RNF02 | **Segurança** — Apenas usuários autenticados com perfil ADMIN ou GERENTE podem acessar o dashboard. |
| RNF03 | **Responsividade** — O painel deve funcionar em telas de desktop (≥992px) e tablet (≥768px). |
| RNF04 | **Compatibilidade** — O frontend utiliza Bootstrap 5 e Chart.js 4, compatíveis com os navegadores modernos (Chrome, Firefox, Edge). |
| RNF05 | **Manutenibilidade** — O código segue o padrão MVC do projeto: Controller → Service → Repository, com DTOs separados. |
| RNF06 | **Disponibilidade** — A API tolera dados incompletos (pedidos sem data de entrada são ignorados nos filtros sem gerar exceção). |
