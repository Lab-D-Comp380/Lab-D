CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    genre VARCHAR(50),
    duration_minutes INT NOT NULL,
    rating VARCHAR(10),
    release_date DATE,
    poster_filename VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS bookings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    movie_id INT NOT NULL,
    ticket_count INT NOT NULL,
    theater VARCHAR(100),
    showtime VARCHAR(50),
    seats VARCHAR(255),
    payment_method VARCHAR(50),
    card_last_four VARCHAR(4),
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

INSERT INTO movies (title, genre, duration_minutes, rating, release_date, poster_filename) VALUES
    ('Skybound', 'Action', 108, 'PG-13', '2026-06-24', 'skybound.png'),
    ('Pixel Quest', 'Animation', 102, 'PG', '2026-06-19', 'pixelquest.png'),
    ('Echo Point', 'Drama', 125, 'PG-13', '2026-06-12', 'echopoint.png'),
    ('Midnight Signal', 'Sci-Fi', 111, 'R', '2026-05-15', 'midnightsignal.png'),
    ('The Last Orbit', 'Sci-Fi', 96, 'PG-13', '2026-06-19', 'lastorbit.png');
