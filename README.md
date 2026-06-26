# Movie Ticket App

JavaFX desktop app for browsing movies and purchasing tickets, backed by a MySQL database running in Docker.

## Prerequisites

- Java 21
- Maven
- Docker Desktop (or Docker Engine + Docker Compose)

## Quick start

### 1. Start MySQL

From the project root:

```bash
docker compose up -d
```

Optional: copy `.env.example` to `.env` if you want to customize database credentials.

Wait until the container is healthy:

```bash
docker compose ps
```

### 2. Run the app

```bash
mvn javafx:run
```

### 3. Create an account

There are no default users. On first launch, click **Register**, choose a username (3+ characters) and password (4+ characters), then sign in.

Movies are pre-seeded in the database when the MySQL container is first created.

## Database configuration

Connection settings live in [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
db.url=jdbc:mysql://localhost:3306/movieapp
db.user=movieapp
db.password=movieapp
```

These defaults match the Docker Compose setup. If you change credentials in `.env`, update `application.properties` to match.

## Docker commands

```bash
docker compose up -d       # start MySQL in the background
docker compose down        # stop the container (data is kept)
docker compose down -v     # stop and wipe all data (re-runs init.sql on next up)
docker compose logs mysql  # view MySQL logs
```

## Project structure

- `App.java` — login/register UI (persists users to MySQL)
- `MovieGalleryView.java` — movie gallery loaded from MySQL
- `docker-compose.yml` — local MySQL staging container
- `docker/mysql/init.sql` — schema and seed data for users and movies

## Troubleshooting

**"Cannot connect to MySQL" on the login screen**

1. Make sure Docker is running.
2. Run `docker compose up -d`.
3. Confirm port 3306 is not in use by another MySQL instance.

**Reset the database**

```bash
docker compose down -v && docker compose up -d
```

This removes all registered users and restores the seeded movies.
