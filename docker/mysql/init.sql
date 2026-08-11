CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255),
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

CREATE TABLE IF NOT EXISTS snacks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    category VARCHAR(50),
    price DECIMAL(6,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS snack_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    snack_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,

    FOREIGN KEY (booking_id) REFERENCES bookings(id),
    FOREIGN KEY (snack_id) REFERENCES snacks(id)
);

CREATE TABLE IF NOT EXISTS reviews (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    movie_id INT NOT NULL,
    rating INT NOT NULL,
    review_text VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

INSERT INTO movies (title, genre, duration_minutes, rating, release_date, poster_filename) VALUES
    ('Skybound', 'Action', 108, 'PG-13', '2026-06-24', 'skybound.png'),
    ('Pixel Quest', 'Animation', 102, 'PG', '2026-06-19', 'pixelquest.png'),
    ('Echo Point', 'Drama', 125, 'PG-13', '2026-06-12', 'echopoint.png'),
    ('Midnight Signal', 'Sci-Fi', 111, 'R', '2026-05-15', 'midnightsignal.png'),
    ('The Last Orbit', 'Sci-Fi', 96, 'PG-13', '2026-06-19', 'lastorbit.png');

INSERT INTO snacks (name, category, price) VALUES
('Small Popcorn', 'Popcorn', 5.99),
('Large Popcorn', 'Popcorn', 8.99),
('Nachos', 'Food', 6.49),
('Hot Dog', 'Food', 5.99),
('Candy', 'Candy', 3.99),
('Chocolate', 'Candy', 4.49),
('Small Soda', 'Drink', 3.49),
('Large Soda', 'Drink', 4.99),
('Water', 'Drink', 2.49);