# Movie Ticket App

JavaFX desktop app for browsing movies and purchasing tickets, backed by a MySQL database running in Docker. Completed bookings show an on-screen receipt and send a copy to the account email via Mailpit.

## Prerequisites

- Java 21
- Maven
- Docker Desktop (or Docker Engine + Docker Compose)

## Quick start

### 1. Start MySQL and Mailpit

From the project root:

```bash
docker compose up -d
```

Wait until the containers are healthy:

```bash
docker compose ps
```

### 2. Run the app

```bash
mvn javafx:run
```

### 3. Create an account

There are no default users. On first launch, click **Register**, choose a username (3+ characters), email address, and password (4+ characters), then sign in.

Movies are pre-seeded in the database when the MySQL container is first created.

### 4. Complete a booking

1. Select a movie from the gallery
2. Pick a showtime
3. Choose seat(s)
4. Enter payment details and confirm purchase
5. View the on-screen receipt
6. Open [http://localhost:8025](http://localhost:8025) in your browser to view the emailed receipt in Mailpit

## Database configuration

Connection settings live in [`src/main/resources/application.properties`](src/main/resources/application.properties):

```properties
db.url=jdbc:mysql://localhost:3306/movieapp
db.user=movieapp
db.password=movieapp
```

## Email (Mailpit)

Receipt emails are sent to the email address on the user's account. Mailpit captures them locally for development:

- SMTP: `localhost:1025`
- Web UI: [http://localhost:8025](http://localhost:8025)

## Docker commands

```bash
docker compose up -d       # start MySQL and Mailpit in the background
docker compose down        # stop containers (data is kept)
docker compose down -v     # stop and wipe all data (re-runs init.sql on next up)
```

## Troubleshooting

**"Cannot connect to MySQL" on the login screen**

1. Make sure Docker is running.
2. Run `docker compose up -d`.
3. Confirm port 3306 is not in use by another MySQL instance.

**Receipt email not received**

1. Make sure Mailpit is running: `docker compose ps`
2. Open [http://localhost:8025](http://localhost:8025) to view captured emails.
3. The on-screen receipt still appears even if email delivery fails.

**Reset the database**

```bash
docker compose down -v && docker compose up -d
```

This removes all registered users and bookings and restores the seeded movies. Required after schema changes (such as adding the `email` column).
