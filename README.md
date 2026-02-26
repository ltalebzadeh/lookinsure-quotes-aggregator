# Insurance Quotes Aggregator

A Spring Boot REST API that aggregates and manages insurance quotes from various providers.

## How to Build & Run

**Prerequisites:** Java 21 installed.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/ltalebzadeh/lookinsure-quotes-aggregator.git
    cd lookinsure-quotes-aggregator/
    ```

2.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```

3.  **Access the API:**
    *   Base URL: `http://localhost:8080/quotes`
    *   H2 Console: `http://localhost:8080/h2-console`
        *   *JDBC URL:* `jdbc:h2:mem:insurancedb`
        *   *User:* `sa`
        *   *Password:* `password`

---
## Caching Strategy

To ensure high performance for expensive operations, this application implements the **Spring Cache** abstraction:

1.  **Read-Heavy Optimization:**
    The aggregation endpoint (`GET /quotes/aggregate`) fetches, filters, and sorts the entire dataset. To prevent database strain, the result of this method is cached in memory (`@Cacheable`).

2.  **Cache Eviction (Consistency):**
    To prevent serving stale data, the cache is automatically invalidated (`@CacheEvict`) whenever the underlying data changes.
    *   **Triggers:** `createQuote`, `updateQuote`, and `deleteQuote` all force a cache clear.
    *   **Result:** Subsequent read requests trigger a fresh database query and update the cache.

---
## Testing

The project includes unit and integration tests:

*   **Unit Tests (`JUnit 5`, `Mockito`):**
    *   Isolated tests for Service layer logic and Controller request/response mapping.
    *   Verifies validation rules and business logic without loading the database.
*   **Integration Tests (`@SpringBootTest`):**
    *   **Cache Verification:** Specifically validates the Caching Strategy within the Spring context to ensure database hits are minimized and eviction works correctly.

To run all tests:
```bash
./mvnw test
```
---

## API Endpoints & Usage

### 1. Get Aggregated Quotes (Best Price)
Returns all quotes sorted by price (Ascending). **This endpoint is cached.**
*   **URL:** `GET /quotes/aggregate`
*   **Response:** `200 OK`

### 2. Create a Quote
Creates a new quote and triggers **cache eviction**.
*   **URL:** `POST /quotes`
*   **Content-Type:** `application/json`
*   **Body:**
    ```json
    {
      "coverageType": "CAR",
      "price": 500.00,
      "providerId": 1
    }
    ```

### 3. Update a Quote
Updates an existing quote and triggers **cache eviction**.
*   **URL:** `PUT /quotes/{id}`
*   **Content-Type:** `application/json`
*   **Body:**
    ```json
    {
      "coverageType": "HEALTH",
      "price": 1200.50,
      "providerId": 2
    }
    ```

### 4. Get All Quotes
Returns a paginated list of quotes.
*   **URL:** `GET /quotes?page=0&size=10`

### 5. Get Single Quote
Returns a specific quote by ID.
*   **URL:** `GET /quotes/{id}`

### 6. Delete a Quote
Deletes a quote and triggers **cache eviction**.
*   **URL:** `DELETE /quotes/{id}`

---

## Tech Stack

*   **Language:** Java 21
*   **Framework:** Spring Boot 3.5.11
    *   *Modules:* Web, Data JPA, Cache, Validation
*   **Database:** H2 In-Memory Database
*   **Tools:**
    *   *MapStruct* (DTO Mapping)
    *   *Lombok* (Boilerplate Reduction)
    *   *JUnit 5 & Mockito* (Testing)

---

## Future Improvements

To prepare this application for a large-scale production environment, the following enhancements are recommended:

*   **Distributed Caching:** Replace the local memory cache with **Redis** to support horizontal scaling across multiple instances.
*   **Containerization:** Add `Dockerfile` and `docker-compose` for consistent deployment and orchestration.
*   **Security:** Implement **Spring Security** to protect write endpoints.
*   **Observability:** Integrate **Spring Actuator** and **Micrometer** for real-time metrics and health monitoring.