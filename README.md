# 🏨 Hotel Maraú | Plataforma de Gestão de Hospedagens

Sistema de gestão de hospedagens para o Hotel Maraú (Maraú/BA), desenvolvido com foco em organização operacional, regras de negócio explícitas e API REST para controle de residências, quartos, clientes e reservas.

O projeto aplica arquitetura em camadas, modelagem com polimorfismo para tipos de quarto e validações de domínio para garantir consistência no processo de reserva.

---

## ✨ Visão Geral

O Hotel Maraú oferece:

- Cadastro e gestão de residências
- Gestão de quartos com tipos especializados (Individual, Duplo e Família)
- Controle de clientes e histórico de hospedagens
- Criação, cancelamento e conclusão de reservas
- Cálculo automático de diárias e valor final com regras de negócio

---

## 🧰 Tecnologias e Arquitetura

### Stack principal

- Java 17
- Spring Boot 3.2
- Spring Web
- Spring Data JPA (Hibernate)
- Spring Validation
- MySQL 8+
- Lombok
- JUnit 5 (via Spring Boot Starter Test)
- Maven

### Arquitetura

Arquitetura em camadas (Layered Architecture):

- Controller: exposição de endpoints REST
- Service: regras de negócio e orquestração
- Repository: acesso a dados com JPA
- Model: entidades e regras de domínio
- DTO: contratos de entrada/saída para API
- Config: inicialização e tratamento global de exceções

---

## 🚀 Setup e Execução

### 1. Pré-requisitos

- Java 17+ instalado
- Maven 3.8+ instalado
- MySQL 8+ em execução

### 2. Criar banco de dados

Execute no MySQL:

```sql
CREATE DATABASE hotel_marau
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar credenciais da aplicação

Edite o arquivo application.properties com seu usuário e senha:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_marau?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=America/Sao_Paulo&allowPublicKeyRetrieval=true
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### 4. Executar a aplicação

No diretório hotel-marau:

```bash
mvn spring-boot:run
```

API disponível em:

- http://localhost:8080

Usuário padrão criado automaticamente na primeira execução (se não existir):

- Email: admin@hotelmarau.com
- Senha: 123456

---

## 🔐 Autenticação

| Método | Rota | Descrição |
|---|---|---|
| POST | /auth/login | Realiza login simples |

Exemplo de body:

```json
{
  "email": "admin@hotelmarau.com",
  "senha": "123456"
}
```

---

## 📡 Endpoints REST

Base URL sugerida: http://localhost:8080

### 🏠 Residências

| Método | Rota | Descrição |
|---|---|---|
| GET | /residencias | Lista todas as residências |
| GET | /residencias/{id} | Busca residência por ID |
| POST | /residencias | Cria residência |
| PUT | /residencias/{id} | Atualiza residência |
| DELETE | /residencias/{id} | Remove residência |

### 🛏️ Quartos

| Método | Rota | Descrição |
|---|---|---|
| GET | /quartos/residencia/{residenciaId} | Lista quartos da residência |
| GET | /quartos/residencia/{residenciaId}/ativos | Lista quartos ativos |
| GET | /quartos/residencia/{residenciaId}/disponiveis | Lista quartos disponíveis por período |
| GET | /quartos/{id} | Busca quarto por ID |
| POST | /quartos/residencia/{residenciaId} | Cria quarto na residência |
| PUT | /quartos/{id} | Atualiza quarto |
| PATCH | /quartos/{id}/ativar | Ativa quarto |
| PATCH | /quartos/{id}/desativar | Desativa quarto |
| GET | /quartos/residencia/{residenciaId}/tipo/{tipo} | Filtra quartos por tipo |
| GET | /quartos/residencia/{residenciaId}/tipo/{tipo}/ativos | Filtra quartos ativos por tipo |
| GET | /quartos/residencia/{residenciaId}/tipo/{tipo}/disponiveis | Filtra quartos disponíveis por tipo e período |

Parâmetros de período em filtros de disponibilidade:

- dataEntrada (ISO 8601)
- dataSaida (ISO 8601)

Tipos de quarto:

- INDIVIDUAL
- DUPLO
- FAMILIA

### 👤 Clientes

| Método | Rota | Descrição |
|---|---|---|
| GET | /clientes | Lista clientes |
| GET | /clientes/{id} | Busca cliente por ID |
| GET | /clientes/cpf/{cpf} | Busca cliente por CPF |
| GET | /clientes/{id}/reservas | Lista reservas do cliente |
| POST | /clientes | Cria cliente |
| PUT | /clientes/{id} | Atualiza cliente |
| DELETE | /clientes/{id} | Remove cliente |
| GET | /clientes/{id}/historico | Histórico completo de hospedagens |
| GET | /clientes/{id}/alugueis/ativos | Reservas confirmadas do cliente |
| GET | /clientes/{id}/alugueis/concluidos | Reservas concluídas do cliente |
| GET | /clientes/{id}/alugueis/cancelados | Reservas canceladas do cliente |
| GET | /clientes/{id}/historico/relatorio | Relatório textual do histórico |

### 📋 Aluguéis (Reservas)

| Método | Rota | Descrição |
|---|---|---|
| GET | /alugueis | Lista aluguéis |
| GET | /alugueis/{id} | Busca aluguel por ID |
| GET | /alugueis/residencia/{residenciaId} | Lista aluguéis por residência |
| POST | /alugueis | Cria aluguel (reserva) |
| PATCH | /alugueis/{id}/cancelar | Cancela reserva |
| PATCH | /alugueis/{id}/concluir | Conclui reserva |
| GET | /alugueis/{id}/recibo | Gera recibo formatado |

Exemplo de payload para criação de reserva:

```json
{
  "residenciaId": 1,
  "quartoId": 2,
  "clienteId": 1,
  "dataEntrada": "2026-07-10T14:00:00",
  "dataSaida": "2026-07-13T11:00:00",
  "numeroHospedes": 2,
  "bercoSolicitado": false
}
```

---

## 📐 Regras de Negócio (Crucial)

### 1) Regra das diárias com referência às 12:00

No cálculo de diárias:

- A referência diária é 12:00
- Entrada após 12:00 conta como diária completa
- Saída após 12:00 adiciona diária extra
- Sempre existe mínimo de 1 diária

### 2) Polimorfismo de quartos

A entidade base Quarto define o contrato de cálculo, e cada subtipo implementa sua regra:

- QuartoIndividual
- QuartoDuplo
- QuartoFamilia

Isso permite encapsular comportamento tarifário por tipo de acomodação.

### 3) Regras por tipo de quarto

#### Quarto Individual

- Valor da diária:
- valorBase
- adicionais comuns (ar e hidro)
- R$ 40 por cama extra a partir da 2ª cama
- Capacidade:
- limite de hóspedes igual ao número de camas
- Berço:
- não permitido

#### Quarto Duplo

- Valor da diária:
- valorBase
- adicionais comuns (ar e hidro)
- adicional por tipo de cama:
- CASAL_COMUM: +R$ 0
- QUEEN: +R$ 60
- KING: +R$ 100
- Berço:
- taxa de R$ 25 por diária quando solicitado
- permitido somente para quarto duplo
- exige que o quarto tenha estrutura de berço (temBerco = true)

#### Quarto Família

- Capacidade máxima calculada por composição de camas:
- solteiro vale 1 hóspede
- casal e queen/king valem 2 hóspedes
- Valor da diária por hóspedes:
- até 2 hóspedes: valorBase + 10%
- 3 a 4 hóspedes: valorBase + 20%
- 5+ hóspedes: valorBase + 30%
- Desconto progressivo sobre o total:
- 4+ hóspedes: 5%
- 6+ hóspedes: 10%
- 8+ hóspedes: 15%

### 4) Adicionais comuns

Aplicáveis aos tipos de quarto conforme configuração:

- Ar-condicionado: +R$ 30 por diária
- Hidromassagem: +R$ 50 por diária

### 5) Disponibilidade e integridade de reserva

Na criação de aluguel, o sistema valida:

- Datas obrigatórias, consistentes e não passadas
- Quarto ativo
- Quarto pertencente à residência informada
- Disponibilidade no período (sem sobreposição de reservas não canceladas)
- Limite de capacidade por tipo de quarto
- Regras de berço por tipo de quarto

---

## 🛡️ Tratamento de Exceções

A aplicação centraliza o tratamento em um ControllerAdvice global, retornando respostas padronizadas com timestamp, status HTTP e mensagem de erro.

### Exceções de domínio customizadas

| Exceção | Quando ocorre | Status HTTP |
|---|---|---|
| QuartoIndisponivelException | Quarto ocupado no período ou desativado | 409 Conflict |
| CapacidadeExcedidaException | Número de hóspedes acima da capacidade | 400 Bad Request |
| DataInvalidaException | Datas inválidas, iguais, no passado ou inconsistentes | 400 Bad Request |
| RecursoNaoPermitidoException | Recurso incompatível (ex.: berço em quarto indevido) | 400 Bad Request |

### Exceções genéricas também tratadas

| Exceção | Status HTTP |
|---|---|
| IllegalArgumentException | 400 Bad Request |
| IllegalStateException | 409 Conflict |
| RuntimeException | 400 Bad Request |
| Exception | 500 Internal Server Error |
| MethodArgumentNotValidException | 400 Bad Request com mapa de erros por campo |

Exemplo de resposta de erro:

```json
{
  "timestamp": "2026-07-01T10:30:00",
  "status": 400,
  "erro": "Data de entrada não pode ser no passado."
}
```

Exemplo de erro de validação por campo:

```json
{
  "timestamp": "2026-07-01T10:30:00",
  "status": 400,
  "erros": {
    "email": "Email é obrigatório"
  }
}
```

---

## ✅ Testes

O projeto possui suíte de testes JUnit cobrindo regras de domínio e cenários integrados.

Para executar todos os testes:

```bash
mvn test
```

Para executar uma classe específica:

```bash
mvn -Dtest=DiariaCalculoTest test
```

---

## 📁 Organização do Projeto

```text
hotel-marau
├─ src
│  ├─ main
│  │  ├─ java/com/hotelmarau
│  │  │  ├─ config
│  │  │  ├─ controller
│  │  │  ├─ dto
│  │  │  ├─ exception
│  │  │  ├─ model
│  │  │  ├─ repository
│  │  │  └─ service
│  │  └─ resources
│  └─ test/java/com/hotelmarau
└─ pom.xml
```

---

## 📌 Observações

- O projeto usa persistência relacional com herança JOINED para tipos de quarto.
- O frontend pode ser servido como recurso estático pela própria aplicação.
- Recomenda-se não versionar credenciais reais no repositório.
