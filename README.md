# Library System API Overview

This project is a Spring Boot-based online library system. Below is an overview of the main REST API endpoints for User, Loan, Book, and Library controllers.

## UserController (`/api/users`)
- `POST /api/users` : Register a new user (member or librarian)
- `POST /api/users/login` : User login
- `GET /api/users/{id}` : Get user details by ID
- `PUT /api/users/{id}` : Update user password
- `DELETE /api/users/{id}` : Delete user by ID

## LoanController (`/api/loans`)
- `POST /api/loans/borrow` : Borrow a book (params: userId, bookId, libraryId)
- `POST /api/loans/return` : Return a borrowed book (param: loanId)
- `GET /api/loans/notify-due-soon` : Notify users whose loans are due within 5 days (simulated)

## BookController (`/api/books`)
- `POST /api/books` : Add a new book (librarian only)
- `PUT /api/books/{bookId}` : Update book information (librarian only)
- `POST /api/books/{bookId}/location` : Add book location to a branch (librarian only)
- `PUT /api/books/{bookId}/location` : Update book location info (librarian only)
- `GET /api/books/search` : Search books by title, author, year

## LibraryController (`/api/libraries`)
- `POST /api/libraries` : Add a new library branch (librarian only)
- `PUT /api/libraries/{id}` : Update library branch info (librarian only)

---
All API responses use a unified JSON format via `ApiResponseDto`:
```
{
  "code": 200,
  "message": "...",
  "data": { ... }
}
```

For authentication and authorization, Spring Security is used. Most management APIs require librarian role.

For more details, see the source code for each controller.
