package com.project.librarymanagement.controller;

import com.project.librarymanagement.dto.AuthorResponse;
import com.project.librarymanagement.dto.BookRequest;
import com.project.librarymanagement.dto.BookResponse;
import com.project.librarymanagement.entity.Author;
import com.project.librarymanagement.entity.Book;
import com.project.librarymanagement.repository.AuthorRepository;
import com.project.librarymanagement.repository.BookRepository;
import com.project.librarymanagement.repository.BorrowingRecordRepository;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    public BookController(
            BookRepository bookRepository,
            AuthorRepository authorRepository,
            BorrowingRecordRepository borrowingRecordRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    // CREATE BOOK
    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody BookRequest request) {

        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A book with this ISBN already exists"
            );
        }

        Set<Author> authors = findAuthors(request.getAuthorIds());

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setAuthors(authors);

        Book savedBook = bookRepository.save(book);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(savedBook));
    }

    // GET ALL BOOKS WITH PAGINATION AND SORTING
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be >= 0 and size must be between 1 and 100"
            );
        }

        Set<String> allowedSortFields =
                Set.of("id", "title", "isbn", "publishedYear");

        if (!allowedSortFields.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid sortBy field"
            );
        }

        Sort.Direction sortDirection;

        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "direction must be asc or desc"
            );
        }

        Pageable pageable = PageRequest.of(
                page, size, Sort.by(sortDirection, sortBy)
        );

        Page<BookResponse> books = bookRepository.findAll(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(books);
    }

    // GET BOOK BY ID
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @PathVariable Long id) {

        Book book = findBook(id);

        return ResponseEntity.ok(toResponse(book));
    }

    // SEARCH BOOK BY ISBN
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BookResponse> getBookByIsbn(
            @PathVariable String isbn) {

        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found with ISBN: " + isbn
                ));

        return ResponseEntity.ok(toResponse(book));
    }

    // UPDATE BOOK
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {

        Book book = findBook(id);

        bookRepository.findByIsbn(request.getIsbn())
                .filter(existingBook -> !existingBook.getId().equals(id))
                .ifPresent(existingBook -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Another book already uses this ISBN"
                    );
                });

        Set<Author> authors = findAuthors(request.getAuthorIds());

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishedYear(request.getPublishedYear());
        book.setAuthors(authors);

        Book updatedBook = bookRepository.save(book);

        return ResponseEntity.ok(toResponse(updatedBook));
    }

    // DELETE BOOK
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {

        Book book = findBook(id);

        if (borrowingRecordRepository.existsByBookId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This book has borrowing history and cannot be deleted"
            );
        }

        bookRepository.delete(book);

        return ResponseEntity.noContent().build();
    }

    private Book findBook(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found with id: " + id
                ));
    }

    private Set<Author> findAuthors(Set<Long> authorIds) {
        List<Author> authors = authorRepository.findAllById(authorIds);

        if (authors.size() != authorIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "One or more author IDs do not exist"
            );
        }

        return new HashSet<>(authors);
    }

    private BookResponse toResponse(Book book) {
        List<AuthorResponse> authorResponses = book.getAuthors()
                .stream()
                .map(author -> new AuthorResponse(
                        author.getId(),
                        author.getName(),
                        author.getBiography()
                ))
                .toList();

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublishedYear(),
                book.isAvailable(),
                authorResponses
        );
    }
}
