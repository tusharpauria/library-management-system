# Library Management System

A RESTful backend application built using Java and Spring Boot to manage books, authors, library members, and book borrowing and returning operations. The application uses PostgreSQL for persistent storage and Flyway for database schema migrations.

## Features

- **Book Management:** Create, retrieve, update, and delete books.
- **Author Management:** Manage author information and associate authors with books.
- **Member Management:** Register, retrieve, update, and delete library members.
- **Borrowing Management:** Borrow available books and record returns.
- **Availability Tracking:** Update book availability when books are borrowed or returned.
- **Borrowing History:** Retrieve borrowing records and active borrowings.
- **Pagination and Sorting:** Retrieve paginated and sorted lists of resources.
- **Input Validation:** Validate incoming request data.
- **Global Exception Handling:** Return structured error responses.
- **Database Migrations:** Manage database schema using Flyway.
- **Integration Testing:** Test borrowing and returning workflows and repository queries.

## Technology Stack

- Java
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Maven
- Jakarta Bean Validation
- JUnit 5
- Spring Boot Test
- MockMvc

## Prerequisites

Install the following before running the application:

- Java version compatible with the project configuration
- Maven, or use the Maven Wrapper if included
- PostgreSQL
- IntelliJ IDEA or another Java IDE
- An API client such as Postman (optional)

## Database Configuration

Create a PostgreSQL database named:

```sql
CREATE DATABASE library_management;
```

The application uses environment variables to configure the database connection.

Configure the following environment variables in your IDE's Run Configuration:

| Variable | Example |
|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/library_management` |
| `DB_USERNAME` | Your PostgreSQL username |
| `DB_PASSWORD` | Your PostgreSQL password |

## Running the Application

### 1. Clone the repository

```bash
git clone https://github.com/tusharpauria/library-management-system.git
cd library-management-system
```

### 2. Configure the database

Create the `library_management` database and configure the three database environment variables described above.

### 3. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Alternatively, open the project in IntelliJ IDEA and run the main Spring Boot application class.

On startup, Flyway applies any pending database migrations. Hibernate validates the database schema.

## API Endpoints

The following endpoints represent the application's main API routes. All endpoints use JSON request and response bodies where applicable.

### Authors

Base path: `/api/authors`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/authors` | Create an author |
| GET | `/api/authors` | Retrieve authors with pagination and sorting |
| GET | `/api/authors/{id}` | Retrieve an author by ID |
| PUT | `/api/authors/{id}` | Update an author |
| DELETE | `/api/authors/{id}` | Delete an author |

### Books

Base path: `/api/books`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/books` | Create a book |
| GET | `/api/books` | Retrieve books with pagination and sorting |
| GET | `/api/books/{id}` | Retrieve a book by ID |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |
| GET | `/api/books/isbn/{isbn}` | Retrieve a book by ISBN |

Books can be associated with authors through the book-author relationship.

### Members

Base path: `/api/members`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/members` | Register a member |
| GET | `/api/members` | Retrieve members with pagination and sorting |
| GET | `/api/members/{id}` | Retrieve a member by ID |
| PUT | `/api/members/{id}` | Update a member |
| DELETE | `/api/members/{id}` | Delete a member |

### Borrowing

Base path: `/api/borrowings`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/borrowings` | Borrow a book |
| GET | `/api/borrowings` | Retrieve borrowing records with pagination and sorting |
| GET | `/api/borrowings/{id}` | Retrieve a borrowing record |
| PUT | `/api/borrowings/{id}/return` | Return a borrowed book |

When a book is borrowed, the application records the borrowing date and due date and marks the book as unavailable. Returning the book records the return date and marks it as available again.

The current borrowing workflow assigns a due date 14 days after the borrowing date.

## Example API Requests

### Create an author

`POST /api/authors`

```json
{
  "name": "Robert Martin",
  "biography": "Software author"
}
```

### Create a book

`POST /api/books`

```json
{
  "title": "Clean Code",
  "isbn": "9780132350884",
  "publishedYear": 2008,
  "authorIds": [1]
}
```

Replace the author ID with an existing author ID in your database.

### Register a member

`POST /api/members`

```json
{
  "name": "John Doe",
  "email": "john@example.com"
}
```

### Borrow a book

`POST /api/borrowings`

```json
{
  "bookId": 1,
  "memberId": 1
}
```

Replace the IDs with existing book and member IDs. The book must be available to borrow.

### Return a book

`PUT /api/borrowings/1/return`

Replace `1` with the ID of an existing borrowing record that has not already been returned.

## Pagination and Sorting

List endpoints support pagination and sorting.

For example:

```http
GET /api/books?page=0&size=10&sortBy=title&direction=asc
```

This example requests the first page of books, with up to 10 records, sorted by title in ascending order.

Use the actual pagination and sorting parameter names supported by each controller.

## Database Schema

The database schema is managed through Flyway migrations stored in:

```text
src/main/resources/db/migration/
```

The initial migration creates tables for:

- `books`
- `authors`
- `book_authors`
- `members`
- `borrowing_records`

The schema includes foreign-key relationships, uniqueness constraints, borrowing-date validation, and indexes for commonly queried fields.

## Running Tests

Run the test suite using:

```bash
mvn test
```

The integration tests cover the borrowing workflow, including borrowing an available book, rejecting a second active borrowing for the same book, returning a borrowed book, and checking repository queries.

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/project/librarymanagement/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── exception/
│   │       ├── repository/
│   │       └── service/
│   └── resources/
│       ├── application.properties
│       └── db/
│           └── migration/
└── test/
    └── java/
        └── com/project/librarymanagement/
            └── BorrowingIntegrationTest.java
```

The package and file structure above reflects the current project organization; adjust it if your actual directories differ.

## Error Handling

The application uses global exception handling to return structured error responses for validation failures, missing resources, invalid requests, and database constraint violations.

## Future Improvements

Potential enhancements include:

- Role-based access control for members and librarians
- Overdue fine calculation
- Notifications for overdue books
- Full-text book search
- Docker-based deployment

These are potential future improvements and should not be considered implemented unless added to the application.

## Author

**Tushar Pauria**

GitHub: [tusharpauria](https://github.com/tusharpauria)