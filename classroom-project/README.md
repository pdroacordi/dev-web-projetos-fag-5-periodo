# Project One - API de Gerenciamento de Turmas

Este projeto é uma API REST para gerenciamento de turmas, desenvolvida com Spring Boot 3.5.4 e Java 21.

## Pré-requisitos

- Java 21
- PostgreSQL 12+
- Gradle 8+

## Configuração do Banco de Dados

1. Instale e configure o PostgreSQL
2. Crie um banco de dados chamado `classrooms`:
```sql
CREATE DATABASE classrooms;
```

3. Configure as credenciais do banco no arquivo `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/classrooms
    username: postgres
    password: postgres
```

## Como Executar

1. Clone o repositório
2. Navegue até o diretório do projeto
3. Execute o comando:
```bash
./gradlew bootRun
```

Ou no Windows:
```cmd
gradlew.bat bootRun
```

A aplicação será iniciada na porta 8080.

## Documentação da API

Após iniciar a aplicação, acesse:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Arquitetura

O projeto segue uma arquitetura em camadas:

- **presentation**: Controllers REST e handlers de exceção
- **application**: DTOs e serviços de aplicação
- **domain**: Entidades e interfaces de repositório
- **infrastructure**: Implementações técnicas (JPA, configurações)

## Tratamento de Erros

- **404 Not Found**: Quando turma não é encontrada
- **400 Bad Request**: Para dados inválidos ou ausência de registros
- **201 Created**: Turma criada com sucesso

## Tecnologias Utilizadas

- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL
- Lombok
- SpringDoc OpenAPI (Swagger)
- Bean Validation