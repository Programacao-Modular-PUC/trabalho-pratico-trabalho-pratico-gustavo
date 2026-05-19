# 🏨 Hotel Maraú 

Sistema de gerenciamento de hospedagens para o Hotel Maraú (Maraú - BA).

---

## ⚙️ Configuração

### Pré-requisitos
- Java 17+
- Maven 3.8+
- MySQL 8+

### 1. Banco de Dados

Crie o banco no MySQL (ou deixe o Spring criar automaticamente):

```sql
CREATE DATABASE hotel_marau CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar credenciais

Edite `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel_marau?createDatabaseIfNotExist=true&...
spring.datasource.username=root
spring.datasource.password=SUA_SENHA_AQUI
```

### 3. Rodar a aplicação

```bash
mvn spring-boot:run
```

A API sobe em: **http://localhost:8080**

Ao iniciar, um usuário padrão é criado automaticamente:
- **Email:** admin@hotelmarau.com
- **Senha:** 123456

---

## 📡 Endpoints

### 🔐 Autenticação

| Método | URL | Descrição |
|--------|-----|-----------|
| POST | `/auth/login` | Login simples |

**Body (login):**
```json
{
  "email": "admin@hotelmarau.com",
  "senha": "123456"
}
```

---

### 🏠 Residências — `/residencias`

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/residencias` | Lista todas |
| GET | `/residencias/{id}` | Busca por ID |
| POST | `/residencias` | Cria nova |
| PUT | `/residencias/{id}` | Atualiza |
| DELETE | `/residencias/{id}` | Remove |

**Body (criar/atualizar):**
```json
{
  "endereco": "Rua das Palmeiras",
  "numero": "42",
  "bairro": "Centro",
  "cep": "45520-000",
  "telefone": "(75) 99999-0000",
  "email": "contato@residencia.com"
}
```

---

### 🛏️ Quartos — `/quartos`

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/quartos/residencia/{id}` | Lista por residência |
| GET | `/quartos/residencia/{id}/ativos` | Lista ativos |
| GET | `/quartos/residencia/{id}/disponiveis?dataEntrada=...&dataSaida=...` | Lista disponíveis |
| GET | `/quartos/{id}` | Busca por ID |
| POST | `/quartos/residencia/{id}` | Cria quarto |
| PUT | `/quartos/{id}` | Atualiza |
| PATCH | `/quartos/{id}/ativar` | Ativa quarto |
| PATCH | `/quartos/{id}/desativar` | Desativa quarto |

**Body — Quarto Individual:**
```json
{
  "tipo": "INDIVIDUAL",
  "valorBase": 150.00,
  "possuiAr": true,
  "possuiHidro": false,
  "numeroCamas": 2
}
```

**Body — Quarto Duplo:**
```json
{
  "tipo": "DUPLO",
  "valorBase": 250.00,
  "possuiAr": true,
  "possuiHidro": true,
  "tipoCama": "QUEEN",
  "temBerco": true
}
```
> `tipoCama`: CASAL_COMUM | QUEEN | KING

**Body — Quarto Família:**
```json
{
  "tipo": "FAMILIA",
  "valorBase": 400.00,
  "possuiAr": true,
  "possuiHidro": false,
  "camasSolteiro": 2,
  "camasCasal": 1,
  "camasQueenKing": 0,
  "numeroAmbientes": 2
}
```

---

### 👤 Clientes — `/clientes`

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/clientes` | Lista todos |
| GET | `/clientes/{id}` | Busca por ID |
| GET | `/clientes/cpf/{cpf}` | Busca por CPF |
| GET | `/clientes/{id}/reservas` | Reservas do cliente |
| POST | `/clientes` | Cria cliente |
| PUT | `/clientes/{id}` | Atualiza |
| DELETE | `/clientes/{id}` | Remove |

**Body:**
```json
{
  "nome": "Ana Lima",
  "cpf": "123.456.789-00",
  "endereco": "Rua A, 10",
  "telefone": "(75) 98888-0000",
  "email": "ana@email.com"
}
```

---

### 📋 Aluguéis — `/alugueis`

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/alugueis` | Lista todos |
| GET | `/alugueis/{id}` | Busca por ID |
| GET | `/alugueis/residencia/{id}` | Por residência |
| POST | `/alugueis` | Cria aluguel |
| PATCH | `/alugueis/{id}/cancelar` | Cancela |
| PATCH | `/alugueis/{id}/concluir` | Conclui |
| GET | `/alugueis/{id}/recibo` | Gera recibo |

**Body (criar):**
```json
{
  "residenciaId": 1,
  "quartoId": 2,
  "clienteId": 1,
  "dataEntrada": "2025-07-10T14:00:00",
  "dataSaida": "2025-07-13T11:00:00",
  "numeroHospedes": 2,
  "bercoSolicitado": false
}
```

---

## 📐 Regras de Negócio

### Cálculo de Diárias
- Referência: **12h00**
- Entrada após 12h → conta como diária completa
- Saída após 12h → adiciona 1 diária extra

### Quarto Individual
- Valor = valorBase + (camas - 1) × R$40 + adicionais
- Limite de hóspedes = número de camas

### Quarto Duplo
- Adicional por tipo: QUEEN +R$60 | KING +R$100
- Berço solicitado: +R$25/diária

### Quarto Família
- Cálculo por hóspedes:
  - até 2: +10% sobre valorBase
  - 3-4: +20%
  - 5+: +30%
- Desconto grupo: 4+ hóspedes (-5%) | 6+ (-10%) | 8+ (-15%)

### Adicionais Comuns
- Ar condicionado: +R$30/diária
- Hidromassagem: +R$50/diária

---

## 🗂️ Estrutura do Projeto

```
src/main/java/com/hotelmarau/
├── HotelMarauApplication.java
├── config/
│   ├── DataInitializer.java
│   └── GlobalExceptionHandler.java
├── controller/
│   ├── AluguelController.java
│   ├── ClienteController.java
│   ├── QuartoController.java
│   ├── ResidenciaController.java
│   └── UsuarioController.java
├── dto/
│   ├── AluguelDTO.java
│   ├── ClienteDTO.java
│   ├── LoginDTO.java
│   ├── QuartoDTO.java
│   └── ResidenciaDTO.java
├── model/
│   ├── Aluguel.java
│   ├── Cliente.java
│   ├── HistoricoHospedagem.java
│   ├── Quarto.java            ← abstract (herança)
│   ├── QuartoDuplo.java
│   ├── QuartoFamilia.java
│   ├── QuartoIndividual.java
│   ├── Residencia.java
│   └── Usuario.java
├── repository/
│   ├── AluguelRepository.java
│   ├── ClienteRepository.java
│   ├── QuartoRepository.java
│   ├── ResidenciaRepository.java
│   └── UsuarioRepository.java
└── service/
    ├── AluguelService.java
    ├── ClienteService.java
    ├── QuartoService.java
    ├── ResidenciaService.java
    └── UsuarioService.java
```
