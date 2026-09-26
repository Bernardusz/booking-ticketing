CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOL NOT NULL
);

CREATE TABLE IF NOT EXISTS movies(
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(2000) NOT NULL,
    release_date DATE NOT NULL,
    duration_minutes INT NOT NULL,
    poster_url TEXT
);

CREATE TABLE IF NOT EXISTS auditoriums(
    id BIGSERIAL PRIMARY KEY,
    total_seats INT NOT NULL
);

CREATE TABLE IF NOT EXISTS languages (
    code VARCHAR(10) PRIMARY KEY,
    title VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS showings (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    start_time TIMESTAMPTZ NOT NULL,
    language_code VARCHAR(10) NOT NULL REFERENCES languages(code) ON DELETE RESTRICT,
    price NUMERIC(10,2) NOT NULL,
    auditorium_id BIGINT NOT NULL REFERENCES auditoriums(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS seats(
    id BIGSERIAL PRIMARY KEY,
    row_label VARCHAR(3) NOT NULL,
    seat_number INT NOT NULL,
    auditorium_id BIGINT NOT NULL REFERENCES auditoriums(id) ON DELETE CASCADE,
    CONSTRAINT unique_auditorium_seat UNIQUE (auditorium_id, row_label, seat_number)
);

CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    CHECK (status IN ('AVAILABLE', 'LOCKED', 'BOOKED')),
    showing_id BIGINT NOT NULL REFERENCES showings(id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    lock_expiration TIMESTAMPTZ,
    CONSTRAINT unique_showing_seat UNIQUE (showing_id, seat_id)
);