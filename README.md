
# Instagram Tracker

A modular Spring Boot application designed to track Instagram profile data and detect changes such as unfollow actions. The system uses modern backend practices including Kafka messaging, Flyway migrations, and Dockerization for portability and scalability.

---

## Features

- RESTful API for creating and updating Instagram profiles
- Kafka consumer for processing unfollow events asynchronously
- Docker-ready for rapid deployment
- API documentation via Swagger UI
- Flyway-based schema versioning
- DTO ↔ Entity mapping using MapStruct
- Console-run integration (CLI mode)

---

## Technologies Used

| Layer               | Tools / Frameworks                                                  |
|--------------------|----------------------------------------------------------------------|
| Language            | Java 17                                                              |
| Framework           | Spring Boot 2.7.4                                                    |
| Database            | MySQL (relational schema: `instagram-tracker`)                      |
| Migrations          | Flyway for repeatable and versioned scripts                         |
| Messaging           | Apache Kafka                                                         |
| Documentation       | Swagger (Springfox configuration)                                    |
| Object Mapping      | MapStruct                                                            |
| REST API            | Spring Web (Controller-Service-Repository structure)                |
| Testing             | JUnit 5 + Mockito (available under `src/test/`)                      |
| Build Tool          | Maven                                                                |
| Containerization    | Docker                                                               |

---

## Architecture Overview

- `ProfileController`: Exposes REST endpoints to manage Instagram profiles.
- `ProfileService`: Business logic layer for profile updates and lookups.
- `KafkaConsumerServiceImpl`: Subscribes to Kafka topics and reacts to unfollow events.
- `ProfileMapper`: MapStruct interface for mapping between DTOs and entity classes.
- `Profile`: JPA Entity mapped to MySQL.
- `SwaggerConfig`: Custom Swagger UI setup.

---

## Kafka Integration

The system includes an async event listener to handle Instagram profile updates via Kafka:

```java
@KafkaListener(topics = "unfollow-events", groupId = "instagram-tracker")
public void consumeEvent(String event) {
    // parse and update profile stats
}
```

You can start Kafka locally or configure it via Docker.

---

## Running the App via Docker

```bash
# Build the Docker image
docker build -t instagram-tracker:latest .

# Run the container
docker run -p 8080:8080 instagram-tracker:latest
```

> Make sure to configure `application.properties` for your local MySQL instance before running.

---

## Database Migrations

Flyway automatically initializes your schema:

- Versioned: `V0.1_0001__CreateTableProfile.sql`
- Configure credentials and schema in `application.properties`

---

## Security

Authentication is not enabled by default. For production, consider adding:
- Basic Auth or JWT
- Service-level token filters (for Kafka payload integrity)

---

## API Documentation

Once the app is running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Future Improvements

- Add Spring Security for endpoint protection
- Add Redis or Caffeine cache for profile lookups
- Expose analytics endpoints (e.g., who unfollowed whom and when)
- Enhance test coverage (unit and integration tests)

---

## Author

Emir Totić
Senior Java Backend Developer
[emirtotic@gmail.com]
[https://www.linkedin.com/in/emirtotic]
