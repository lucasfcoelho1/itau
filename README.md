# 🐶 Pet Suggestion API

Esta API RESTful sugere a melhor raça de cachorro com base nas características de um país informado. Ela integra com APIs públicas e utiliza IA para compor a sugestão final.

## 🛠️ Tecnologias e bibliotecas

- **Java 21**
- **Spring Boot 3**
- **Spring Web**
- **Spring Retry**
- **Resilience4j**
- **Spring Cache + Redis**
- **Spring Security + JWT**
- **Lombok**
- **Swagger/OpenAPI**
- **MySQL**
- **Docker + Docker Compose**
- **JUnit + Mockito**

## 🧱 Arquitetura da aplicação

A aplicação segue princípios da **Arquitetura Hexagonal**:


```
src/
├── adapter/
├── controller/
├── diplomat/
│   ├── http-out/
│   └── wire-in/
├── models/
├── service/
├── pure-logic/
```

![Arquitetura Proposta](/arquitetura-hexagonal.png)

## 🔐 Segurança com JWT

Para obter um token:

```bash
GET /auth/token?user=lucas
Content-Type: application/json

```

Header de autenticação:

```
Authorization: Bearer <token>
```

## 🌐 Documentação dos Endpoints (Swagger)

```
http://localhost:8080/swagger-ui/index.html
```

## 📦 Endpoints principais

### ✅ Sugerir pet por país

```http
GET /api/pet-suggestion/countries/{countryName}
```

Resposta:

```json
{
  "country": {
    "title": "Noruega",
    "region": "Europe"
  },
  "pet": {
    "breed": "Husky Siberiano",
    "description": "Companheiro ideal para climas frios e aventuras ao ar livre"
  }
}
```

### 🌍 Endpoint: Buscar todos os países
```http 
GET /api/countries
```
### ✅ Exemplos de uso:

### 1. Buscar todos os países:
```http 
GET /api/countries
Authorization: Bearer <jwt_token>
```
### 2. Buscar países com nome contendo “bra”:
```http 
GET /api/countries?name=bra
Authorization: Bearer <jwt_token>
```
### 🔁 Resposta:
```json
[
  {
    "name": "Brazil",
    "region": "Americas",
    "population": 212559417
  },
  {
    "name": "Brandenburg",
    "region": "Europe",
    "population": 2500000
  }
]
```

## ⚙️ Tratamento de exceções

- `ExternalServiceException` → `502 Bad Gateway`
- `ResponseParseException` → `500 Internal Server Error`
- `RateLimitExceededException` → `429 Too Many Requests`

## 🧠 IA e sugestões personalizadas

Prompt dinâmico baseado nos dados do país enviados a uma API de IA. Retorno é processado e validado antes de ser entregue ao cliente.

## ⚡ Cache com Redis

- Sugestões e países são cacheados por 1 hora
- TTL controlado via `CacheService`

## 🛡️ Tolerância a falhas

- `@Retryable`
- `@CircuitBreaker`
- `@Recover` para fallback

## 🐳 Como rodar localmente

### 1. Sem Docker

```bash
./mvnw spring-boot:run
```

### 2. Com Docker Compose

```bash
docker-compose up --build
```

## 🧪 Testes

```bash
./mvnw test
```

```bash
docker build -t pet-suggestion-api .
```

## 🧠 Diferenciais técnicos

- Arquitetura hexagonal
- IA para sugestões
- Tolerância a falhas
- Cache eficiente
- Código testável
- Segurança com JWT
