# Vayikra

Vayikra is a Kotlin/Ktor backend project for exploring how to build a book-sharing platform for Hebrew books in Europe.

The project is currently in early development and is used to practice backend architecture with Kotlin, Ktor, PostgreSQL, Exposed, authentication, and API design.

## Name

**Vayikra** comes from Hebrew **וַיִּקְרָא** (*vayikra*), meaning “and he called”. It is also the Hebrew name of the Book of Leviticus.

The name is also a small wordplay: the Hebrew root **ק־ר־א** means both “to call” and “to read”, 
connecting the project’s focus on books with the idea of calling people to share and read Hebrew texts.

## Current status

Vayikra is in early development.

The repository currently contains a Kotlin/Ktor backend setup with:

- Ktor server
- PostgreSQL database support
- Exposed ORM / SQL toolkit
- JWT authentication setup
- JSON serialization
- Docker-based local database
- Gradle build configuration
- ktlint formatting / linting

## Planned product direction

The long-term idea is to build a platform for sharing Hebrew and Jewish books, especially for people living outside Israel who may have limited access to physical Hebrew books.

Possible future features:

- Book listings
- Search by title, author, language or category
- User accounts
- Book availability status
- Lending / sharing workflow
- Location-aware discovery
- Personal libraries
- Basic moderation or review flow

## Tech stack

- Kotlin
- Ktor
- Gradle
- PostgreSQL
- Exposed
- Docker
- JWT authentication
- kotlinx.serialization
- ktlint

## Repository structure

```txt
.github/              GitHub configuration
gradle/               Gradle wrapper and configuration
src/                  Kotlin application source code
build.gradle.kts      Gradle build configuration
docker-compose.yml    Local PostgreSQL setup
env.example           Example environment variables
settings.gradle.kts   Gradle project settings
```

## Local development

### Requirements

- JDK 21
- Docker
- Gradle Wrapper

### Environment variables

Create a local `.env` file based on:

```bash
cp env.example .env
```

### Start the database

```bash
docker compose up -d
```

### Run the application

```bash
./gradlew run
```

The server should start on:

```txt
http://0.0.0.0:8080
```

## Development commands

| Command | Description |
|---|---|
| `./gradlew run` | Runs the Ktor application |
| `./gradlew build` | Builds the project |
| `./gradlew test` | Runs tests |
| `./gradlew ktlintCheck` | Checks Kotlin formatting |
| `./gradlew ktlintFormat` | Formats Kotlin code |
| `docker compose up -d` | Starts the local PostgreSQL database |
| `docker compose down` | Stops the local Docker services |

## Roadmap

- Define the initial domain model
- Add book entities
- Add user entities
- Add authentication flow
- Add lending / sharing workflow
- Add search endpoints
- Add tests
- Add API documentation
- Improve README with real API examples

## License

License to be decided.