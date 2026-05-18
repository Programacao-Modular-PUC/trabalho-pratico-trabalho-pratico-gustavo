# Hotel Marau — Backend

Este repositório contém o backend Spring Boot do **Hotel Marau**.

## Estrutura
- `hotel-marau/` → projeto Maven/Spring Boot (código, controllers/services/repositories, etc.)
- `index.html` → arquivo estático (se estiver sendo usado na entrega)
- `PM_TRABALHOPRATICO-1.pdf`, `SPRINT2_TPPM.pdf` → documentação da disciplina/sprints

## Rodar localmente
1. Entre no diretório do backend:
   ```bash
   cd hotel-marau
   ```
2. Ajuste as credenciais no arquivo:
   - `src/main/resources/application.properties`
3. Rode a aplicação:
   ```bash
   mvn spring-boot:run
   ```

A API sobe em: `http://localhost:8080`.

## Notas sobre organização
- A pasta `hotel-marau/target/` é gerada automaticamente pelo Maven e está ignorada via `.gitignore`.

