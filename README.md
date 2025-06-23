# Golden Raspberry Awards API

Este projeto é parte de um desafio técnico, com o objetivo de desenvolver uma API RESTful que possibilita a leitura da lista de indicados e vencedores da categoria **Pior Filme** do Golden Raspberry Awards.

## Funcionalidade

A API expõe um endpoint que retorna o(s) produtor(es) com:

- **Menor intervalo** entre duas vitórias consecutivas
- **Maior intervalo** entre duas vitórias consecutivas

Os dados são lidos de um arquivo CSV e armazenados em um banco de dados em memória (H2) no início da aplicação.

---

## Tecnologias utilizadas

- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- H2 Database (em memória)
- Flyway (versionamento do banco de dados)
- Spring Cache
- JUnit
- Maven

---

## Como rodar o projeto

### Pré-requisitos

- Java 21 instalado
- Maven 3.8+ instalado

### Clonar o repositório

```bash
git clone https://github.com/hudovinicius/tech-challenge-razzies.git
cd tech-challenge-razzies
```

### Rodar a aplicação
```bash
mvn clean install
mvn spring-boot:run
```
A aplicação estará disponível em: http://localhost:8080

---

## Rodar os testes
```bash
mvn test
```
Os testes de integração garantem que a API está processando os dados corretamente com base no CSV fornecido.

---
## Documentação da API
Interface Swagger disponível em: http://localhost:8080/swagger-ui.html

### GET /v1/producers/intervals 
Endpoint para obter os produtores com menor e maior intervalo entre vitórias consecutivas:

```bash
curl http://localhost:8080/v1/producers/intervals
```

```http
## Request

GET /v1/producers/intervals

## Response

{
    "min": [
        {
            "producer": "Joel Silver",
            "interval": 1,
            "previousWin": 1990,
            "followingWin": 1991
        }
    ],
    "max": [
        {
            "producer": "Matthew Vaughn",
            "interval": 13,
            "previousWin": 2002,
            "followingWin": 2015
        }
    ]
}
```

---
## Observações
- O projeto segue a arquitetura em camadas (MVC).
- O banco de dados é resetado e recriado a cada execução.
- O CSV de entrada está localizado em: src/main/resources/csv/Movielist.csv.
- A cache é aplicada no cálculo de intervalos para melhorar performance.

## Autor
Hudo Salvador

## Licença
Este projeto é de código aberto sob a licença MIT. Sinta-se à vontade para contribuir e melhorar o projeto.