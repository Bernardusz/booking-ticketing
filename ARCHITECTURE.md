# Tech Stack
- Analog.js (Frontend)
- Spring Boot - Kotlin (Backend & Language)
- Sprng Data JPA + Hibernate & JdbcClient (SQL)
- Virtual Threads (Concurency)
- PostgreSQL (Database)
- Maven (Build tools)

# Database Schema

## 1. Movie
- title (Varchar 100)
- description (Varchar 2000)
- release_date (DATE)
- duration_minutes (INT) - In Minutes
- poster_url (Link)

## 2. Showing
- id (BIGINT)
- movie_id (Foreign Key - Movie)
- start_time (Timestampz)
- language_code (Foreign Key - For Language filtering)
- price (DECIMAL)
- auditorium_id (Foreign Key)

## 3. Language
- title (Varchar 20)
- code (VARCHAR)

## 4. Auditorium 
- id (BIGINT)
- total_seats

## 5. Seat
- id (BIGINT)
- row_label
- seat_number
- auditorium_id (Foreign Key)

## 6. Ticket
- id (BIGINT)
- status (AVAILABLE | LOCKED | BOOKED)
- showing_id (Foreign Key)
- seat_id (Foreign Key - Foreign Key, because many movies may use the same seat at a different time)
- user_id (Foreign Key)
- lock_expiration (Timestampz)

## 7. User
- id (BIGINT)
- username (VARCHAR - 30)
- password (TEXT Hashed & Salted)
- refresh_token (UUID - Hashed & Salted)
