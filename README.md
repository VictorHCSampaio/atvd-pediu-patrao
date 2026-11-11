# Como Rodar o Projeto

## Pré-requisitos

- Java 21+
- Maven (ou use o `./mvnw` incluído no projeto)
- MongoDB (local ou Atlas)

## Configuração

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```
MONGODB_URI=mongodb://localhost:27017
MONGODB_DATABASE=pizzaria
```

> Se estiver usando MongoDB Atlas, substitua a URI pela string de conexão do seu cluster.

## Rodando

```bash
./mvnw spring-boot:run
```

Ou no Windows:

```bash
mvnw.cmd spring-boot:run
```

## Acesso

Após subir, acesse: `http://localhost:8080`

Na primeira execução, o sistema cria automaticamente os seguintes usuários no MongoDB:

| Usuário    | Senha      | Perfil    |
|------------|------------|-----------|
| `admin`    | `teste123` | ADMIN     |
| `gerente`  | `teste123` | GERENTE   |
| `atendente`| `teste123` | ATENDENTE |
