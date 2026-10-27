package com.project.librarymanagement.controller;

import com.project.librarymanagement.dto.AuthorRequest;
import com.project.librarymanagement.dto.AuthorResponse;
import com.project.librarymanagement.entity.Author;
import com.project.librarymanagement.repository.AuthorRepository;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository authorRepository;

    public AuthorController(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // CREATE AUTHOR
    @PostMapping
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {

        Author author = new Author();
        author.setName(request.getName());
        author.setBiography(request.getBiography());

        Author savedAuthor = authorRepository.save(author);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(savedAuthor));
    }

    // GET ALL AUTHORS WITH PAGINATION AND SORTING
    @GetMapping
    public ResponseEntity<Page<AuthorResponse>> getAllAuthors(
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

        // Only permit sorting by these fields
        if (!sortBy.equals("id") && !sortBy.equals("name")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "sortBy must be id or name"
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
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        Page<AuthorResponse> authors = authorRepository.findAll(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(authors);
    }

    // GET AUTHOR BY ID
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable Long id) {

        Author author = findAuthor(id);

        return ResponseEntity.ok(toResponse(author));
    }

    // UPDATE AUTHOR
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponse> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest request) {

        Author author = findAuthor(id);

        author.setName(request.getName());
        author.setBiography(request.getBiography());

        Author updatedAuthor = authorRepository.save(author);

        return ResponseEntity.ok(toResponse(updatedAuthor));
    }

    // DELETE AUTHOR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {

        Author author = findAuthor(id);

        authorRepository.delete(author);

        return ResponseEntity.noContent().build();
    }

    private Author findAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Author not found with id: " + id
                ));
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBiography()
        );
    }
}
