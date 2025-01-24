# Features

- Reactive (Non-Blocking) Rest API
  + Journal Entry API
  + Account Balance API
  + Documented request and response model
- Request Validation
  + JSR validated Journal Entry requests
  + Coded/Internationalized validation response messages (message.properties)
  + Custom constraint validation utilizing persisted data lookups
- JCache read-through cached data access
- PostgreSQL database access
  + R2DBC reactive database access
  + JDBC synchronous database access
  + Auto-configured Spring Data repositories
  + Parameterized custom database queries
- Versioned database migrations (Flyway)
- Async background event consumers for balance analytics aggregation
  + (Optional) Kafka topic event writing
  + (Optional) Kafka async event consumer for balance aggregation
  + (Default) In-memory async event consumer for balance aggregation
- Production Ready Features
  + Configuration through environment variables at runtime
  + JSON formatted service logging for DataDog ingest
  + Health-check endpoint (Spring Boot Actuator)
  + JMX metric publication for DataDog collection
  + Configurable service log levels (Logback)
  + Configurable runtime profiles (`kafka`, `datadog`) enable profile specific features and configurations
- CI/CD
  + Configured GitHub actions pipeline (`.github/workflows/gradle.yml`)
  + Gradle 8.11 (Gradle Wrapper)
  + Static code analysis (Detekt), with publishable report
  + Linting and formatting (KtLint), with publishable report
  + Unit and Integration tests (JUnit), with publishable report
  + Published dependency analysis, Dependabot integration

# Getting Started

## Nix-based Development Environment

This project uses [devenv.sh](https://devenv.sh/) to manage the development environment. To build and run the project:

1. Install the [hi-devenv development environment](https://docs.app-dev.inside.humaninterest.com/shared-functions/environment/nix/getting-started)
2. Run `devenv up` in the project directory

## Manual Environment Setup

### Requirements

- Java 21
- PostgreSQL

A PostgresSQL database instance, with a user: `demo` and password `demo` with permission to create databases and schemas.
If those are in place, no other configuration is necessary. Otherwise, review the table below:

| Name                      | Description                                                      | Default     |
|---------------------------|------------------------------------------------------------------|-------------|
| `APP_DB_USERNAME`         | DB username                                                      | `demo`      |
| `APP_DB_PASSWORD`         | DB password                                                      | `demo`      |
| `APP_DB_HOST`             | DB hostname                                                      | `127.0.0.1` |
| `APP_DB_PORT`             | DB port                                                          | `5432`      |
| `APP_DB_DATABSE`          | DB database name                                                 | `demo`      |
| `APP_DB_SCHEMA`           | DB schema name                                                   | `demo`      |
| `KAFKA_BOOTSTRAP_SERVERS` | (optional) Kafka bootstrap servers if `kafka` profile is enabled |             |
| `KAFKA_KEY`               | (optional) Kafka secret key `kafka` profile is enabled           |             |
| `KAFKA_SECRET`            | (optional) Kafka secret `kafka` profile is enabled               |             |
| `KAFKA_CLIENT_ID`         | (optional) Kafka client id `kafka` profile is enabled            |             |
| `SPRING_PROFILES_ENABLED` | (optional) Comma separated string of profiles to enable          |             |

Full configuration details can be view at `src/main/kotlin/resources/application.yml`

### Available Profiles
- `kafka` - Enables Kafka integration, requires all `KAFKA_*` environment variables to be set
- `datadog` - Enables JSON logging format and MDC configurations

### Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.0/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.0/gradle-plugin/packaging-oci-image.html)
* [Coroutines section of the Spring Framework Documentation](https://docs.spring.io/spring-framework/reference/6.2.0/languages/kotlin/coroutines.html)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.4.0/reference/actuator/index.html)
* [Spring Data R2DBC](https://docs.spring.io/spring-boot/3.4.0/reference/data/sql.html#data.sql.r2dbc)
* [Flyway Migration](https://docs.spring.io/spring-boot/3.4.0/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
* [Validation](https://docs.spring.io/spring-boot/3.4.0/reference/io/validation.html)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/3.4.0/reference/web/reactive.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Accessing data with R2DBC](https://spring.io/guides/gs/accessing-data-r2dbc/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)

### Additional Links

These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
* [R2DBC Homepage](https://r2dbc.io)

