# SB E-Commerce API

A Spring Boot REST API for an e-commerce application, with JWT authentication, role-based access, product and category management, shopping carts, checkout, and seller-specific order fulfillment.

**Author:** Surya Potti

## Features

- User registration and sign-in with BCrypt password hashing and JWT bearer tokens.
- Public registration assigns `ROLE_USER`; administrators can grant seller and administrator roles.
- Category creation, updates, and deletion for administrators.
- Product management for sellers, with ownership checks for updates and deletion.
- Product search, category filtering, active listings, pagination, and sorting.
- Address management for the signed-in user, with administrator-only global listings.
- Shopping cart operations: add products, change quantities, remove items, and view totals.
- Checkout for the full cart or one product already in the cart.
- Transactional checkout with stock availability checks and conditional stock decrements.
- Monetary calculations using `BigDecimal`.
- Separate fulfillment records for each seller participating in an order.
- Seller order lists, status filtering, and sequential fulfillment updates.
- Request validation, centralized exception handling, and audit fields.
- Swagger/OpenAPI documentation and JUnit/Mockito tests with JaCoCo coverage checks.

## Technology Stack

| Component | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot 3.5.7 |
| API | Spring Web |
| Security | Spring Security, JJWT 0.13.0 |
| Persistence | Spring Data JPA / Hibernate, MySQL |
| Validation | Jakarta Bean Validation |
| Mapping and boilerplate | ModelMapper 3.2.4, Lombok |
| API documentation | Springdoc OpenAPI 2.8.15 |
| Testing | Spring Boot Test, JUnit Jupiter, Mockito |
| Coverage | JaCoCo 0.8.15 |
| Build | Maven; wrapper configured for 3.9.11 |

## Prerequisites

- JDK 21.
- A running MySQL server and an account with access to the application database.
- Internet access for the first Maven dependency download.
- Optional: IntelliJ IDEA and Postman.

The Maven wrapper is included, so a separate Maven installation is optional.

## Local Setup

### 1. Open the project

Extract the project or clone your repository, then open a terminal in the `sb-ecom` directory containing `pom.xml`.

```bash
cd sb-ecom
chmod +x mvnw
java -version
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

### 2. Create the database

Run in MySQL:

```sql
CREATE DATABASE IF NOT EXISTS sbecom;
```

### 3. Configure the application

Edit `src/main/resources/application.properties`. Replace the supplied local credentials and JWT secret with environment-backed settings:

```properties
spring.application.name=sb-ecom
server.port=8080

spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/sbecom}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.app.jwtSecret=${JWT_SECRET}
spring.app.jwtExpirationMs=${JWT_EXPIRATION_MS:3600000}
spring.app.jwtCookie.name=sb-ecom-jwt-cookie
```

Set these variables in your shell or IntelliJ run configuration. The following example is for macOS/Linux; replace the database placeholders:

```bash
export DB_USERNAME='your_mysql_username'
export DB_PASSWORD='your_mysql_password'
export JWT_SECRET="$(openssl rand -base64 32)"
export JWT_EXPIRATION_MS='3600000'
```

`JwtUtils` Base64-decodes the secret, so use a Base64-encoded random key of at least 32 bytes. Keep the same secret between runs if existing tokens should remain valid. Do not commit real credentials. The example expiry is one hour; the uploaded configuration uses `300000000` milliseconds.

The application reads JWTs from the `Authorization` header; the cookie-name property does not enable cookie authentication.

### 4. Start the application

```bash
./mvnw spring-boot:run
```

- Base URL: <http://localhost:8080>
- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- OpenAPI JSON: <http://localhost:8080/v3/api-docs>

With `ddl-auto=update`, Hibernate creates or updates mapped tables on startup. This setting is intended here for local development.

### 5. Initialize roles

The project does not include an automatic role initializer. After the first successful startup creates the tables, run the following against `sbecom` before registering users:

```sql
USE sbecom;

INSERT INTO role (role_name)
SELECT 'ROLE_USER'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_USER');

INSERT INTO role (role_name)
SELECT 'ROLE_SELLER'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_SELLER');

INSERT INTO role (role_name)
SELECT 'ROLE_ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE role_name = 'ROLE_ADMIN');
```

For the first administrator, register your own account and assign its administrator role through a trusted local database session. Replace `your_admin_username` with that account's username:

```sql
INSERT INTO user_role (user_id, role_id)
SELECT u.user_id, r.role_id
FROM users u
JOIN role r ON r.role_name = 'ROLE_ADMIN'
WHERE u.username = 'your_admin_username'
  AND NOT EXISTS (
      SELECT 1 FROM user_role ur
      WHERE ur.user_id = u.user_id AND ur.role_id = r.role_id
  );
```

After this initial setup, use the administrator role-grant endpoint for subsequent assignments. These SQL examples use the table and column names generated by the project's default naming configuration.

## Authentication

Register an account:

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H 'Content-Type: application/json' \
  -d '{"userName":"demo_user","emailId":"demo@example.com","password":"Demo123!"}'
```

Sign in:

```bash
curl -X POST http://localhost:8080/api/auth/signin \
  -H 'Content-Type: application/json' \
  -d '{"username":"demo_user","password":"Demo123!"}'
```

Signup uses `userName`, while sign-in uses `username`. The current signup DTO accepts usernames of 3–20 characters and passwords of 3–10 characters.

Copy `jwtToken` from the sign-in response and send it with protected requests:

```bash
curl http://localhost:8080/app/public/products \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN'
```

In Swagger UI, select **Authorize** and paste the token into the bearer authentication field.

## API Endpoints

All paths below are relative to `http://localhost:8080`. Except for authentication routes and API documentation, requests require a valid JWT. Despite their names, `/app/public/**` routes currently require authentication.

### Authentication and roles

| Method | Endpoint | Purpose / access |
| --- | --- | --- |
| POST | `/api/auth/signup` | Register a user; no login required |
| POST | `/api/auth/signin` | Obtain a JWT; no login required |
| GET | `/api/auth/username` | Current username; empty when unauthenticated |
| PUT | `/api/admin/users/{userId}/roles/{roleName}` | Grant `ROLE_ADMIN` or `ROLE_SELLER`; administrator only |

### Categories and products

| Method | Endpoint | Purpose / access |
| --- | --- | --- |
| GET | `/app/public/category` | List categories |
| GET | `/app/public/category/active` | List active categories |
| GET | `/app/public/category/{categoryId}` | Get a category |
| POST | `/app/admin/category` | Create a category; administrator |
| PUT / DELETE | `/app/admin/category/{categoryId}` | Update / delete a category; administrator |
| GET | `/app/public/products` | List products |
| GET | `/app/public/products/active` | List active products |
| GET | `/app/public/product/{productId}` | Get a product |
| GET | `/app/public/category/{categoryId}/products` | List products by category |
| GET | `/app/public/product/keyword/{keyword}` | Search products |
| POST | `/app/seller/categories/{categoryId}/product` | Create a product; seller |
| PUT / DELETE | `/app/seller/product/{productId}` | Update / delete an owned product; seller |

### Addresses and carts

| Method | Endpoint | Purpose / access |
| --- | --- | --- |
| POST | `/api/addresses` | Create an address |
| GET | `/api/addresses/user` | List the current user's addresses |
| GET / PUT / DELETE | `/api/addresses/{addressId}` | Read / update / delete an owned address |
| GET | `/api/addresses` | List all addresses; administrator |
| POST | `/api/carts/products/{productId}/quantity/{quantity}` | Add a product to the cart |
| PUT | `/api/carts/products/{productId}/quantity/{quantity}` | Update cart product quantity |
| DELETE | `/api/carts/product/{productId}` | Remove a cart product |
| GET | `/api/carts/user/cart` | Get the current user's cart |
| GET | `/api/carts` | List all carts; administrator |

### Checkout and seller fulfillment

| Method | Endpoint | Purpose / access |
| --- | --- | --- |
| POST | `/api/orders` | Check out the entire cart |
| POST | `/api/orders/product/{productId}` | Check out one product already in the cart |
| GET | `/app/seller/orders` | List the current seller's orders, including delivered orders |
| GET | `/app/seller/orders/{status}` | Filter the current seller's orders by status |
| PATCH | `/app/seller/orders/{sellerOrderId}/status` | Update an owned seller order's status |

Checkout requires an address owned by the signed-in user. A minimal request body is:

```json
{
  "addressId": 1,
  "paymentType": "COD"
}
```

Use an existing address ID. The single-product endpoint uses that product's quantity from the cart. Payment gateway metadata can be recorded, but the current service does not integrate with a live payment gateway or independently verify payment.

Seller fulfillment follows this sequence:

`PLACED` → `PACKED` → `IN_TRANSIT` → `OUT_FOR_DELIVERY` → `DELIVERED`

Skipping steps or moving backward is rejected. Sending the existing status returns the existing record. Example status update body:

```json
{
  "orderStatus": "PACKED",
  "remark": "Ready for dispatch"
}
```

The current order controller exposes checkout endpoints only; customer order-history and order-tracking GET endpoints are not included.

## Pagination and Sorting

Category, product, and seller-order list endpoints accept:

| Parameter | Default | Description |
| --- | --- | --- |
| `pageNumber` | `0` | Zero-based page index |
| `pageSize` | `10` | Number of results per page |
| `sortBy` | Resource-specific | Entity field used for sorting |
| `sortOrder` | `asc` | Use `asc` or `desc` |

Default sort fields are `categoryId`, `productId`, and `createdOn` for categories, products, and seller orders respectively.

```text
/app/public/products?pageNumber=0&pageSize=10&sortBy=productId&sortOrder=desc
```

Paginated responses include `content`, `pageNumber`, `pageSize`, `totalElements`, `totalPages`, and `lastPage`.

## Tests and Code Coverage

Run tests:

```bash
./mvnw clean test
```

Build and verify the project:

```bash
./mvnw clean verify
```

The current `pom.xml` attaches the JaCoCo agent and binds both report generation and coverage checking to the **test** phase. The configured minimum is **80% aggregate line coverage across the project bundle**, not 80% for every class. Coverage below that threshold causes the build to fail.

| Output | Path |
| --- | --- |
| HTML coverage report | `target/site/jacoco/index.html` |
| Raw coverage data | `target/jacoco.exec` |
| Test reports | `target/surefire-reports/` |

Open the HTML report on macOS:

```bash
open target/site/jacoco/index.html
```

If only `jacoco.exec` exists, inspect the Maven output for test or plugin failures. To explicitly render existing coverage data after classes have been compiled:

```bash
./mvnw jacoco:report
```

This report command alone does not execute tests or enforce the coverage threshold. The threshold describes the configured requirement; it is not a claim about the current measured coverage.

## Package and Run

```bash
./mvnw clean package
java -jar target/sb-ecom-0.0.1-SNAPSHOT.jar
```

Packaging runs tests and the coverage gate. Keep the database and environment variables configured when running the JAR.

## Project Structure

| Path | Responsibility |
| --- | --- |
| `src/main/java/com/ecommerce/project/controller/` | REST endpoints |
| `src/main/java/com/ecommerce/project/service/` | Business logic |
| `src/main/java/com/ecommerce/project/repository/` | Database access |
| `src/main/java/com/ecommerce/project/model/` | JPA entities and enums |
| `src/main/java/com/ecommerce/project/payload/` | Data transfer objects |
| `src/main/java/com/ecommerce/project/response/` | Response wrappers |
| `src/main/java/com/ecommerce/project/security/` | Security configuration, JWT handling, and authentication models |
| `src/main/java/com/ecommerce/project/config/` | ModelMapper and OpenAPI configuration |
| `src/main/java/com/ecommerce/project/exception/` | Exceptions and centralized handlers |
| `src/main/java/com/ecommerce/project/constants/` | Shared defaults and status constants |
| `src/main/java/com/ecommerce/project/util/` | Authentication utilities |
| `src/main/resources/application.properties` | Application configuration |
| `src/test/java/` | Automated tests |
| `pom.xml` | Dependencies and build configuration |

## Troubleshooting

| Problem | Check |
| --- | --- |
| MySQL connection or access error | Database is running; `sbecom` exists; URL, credentials, and grants are correct |
| `ROLE_USER is missing from the database` | Initialize the role records before signup |
| JWT key or decoding error | `JWT_SECRET` is valid Base64 and decodes to at least 32 bytes |
| `401 Unauthorized` | Supply a valid, unexpired bearer token |
| `403 Forbidden` | Use an account with the required role and an owned resource where applicable |
| `Permission denied` when running the wrapper | Run `chmod +x mvnw` on macOS/Linux |
| Build fails at the JaCoCo check | Open the report and improve tests for uncovered behavior |
| No HTML coverage report | Check test completion and run `./mvnw jacoco:report` if execution data exists |

