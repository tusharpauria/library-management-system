package com.project.librarymanagement.controller;

import com.project.librarymanagement.dto.BorrowingRequest;
import com.project.librarymanagement.dto.BorrowingResponse;
import com.project.librarymanagement.service.BorrowingService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    // BORROW A BOOK
    @PostMapping
    public ResponseEntity<BorrowingResponse> borrowBook(@Valid @RequestBody BorrowingRequest request) {

        BorrowingResponse response = borrowingService.borrowBook(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // RETURN A BOOK
    @PutMapping("/{id}/return")
    public ResponseEntity<BorrowingResponse> returnBook(@PathVariable Long id) {

        BorrowingResponse response = borrowingService.returnBook(id);

        return ResponseEntity.ok(response);
    }

    // GET BORROWING RECORD BY ID
    @GetMapping("/{id}")
    public ResponseEntity<BorrowingResponse> getBorrowingById(@PathVariable Long id) {

        return ResponseEntity.ok(
                borrowingService.getBorrowingById(id)
        );
    }

    // GET ALL BORROWING RECORDS WITH PAGINATION AND SORTING
    @GetMapping
    public ResponseEntity<Page<BorrowingResponse>> getAllBorrowings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be >= 0 and size must be between 1 and 100"
            );
        }

        Set<String> allowedSortFields =
                Set.of("id", "borrowedAt", "dueDate", "returnedAt");

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
                page,
                size,
                Sort.by(sortDirection, sortBy)
        );

        return ResponseEntity.ok(
                borrowingService.getAllBorrowings(pageable)
        );
    }
}
