package com.project.librarymanagement.dto;

import java.util.List;

public class BookResponse {

    private Long id;
    private String title;
    private String isbn;
    private Integer publishedYear;
    private boolean available;
    private List<AuthorResponse> authors;

    public BookResponse() {
    }

    public BookResponse(
            Long id,
            String title,
            String isbn,
            Integer publishedYear,
            boolean available,
            List<AuthorResponse> authors) {

        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.available = available;
        this.authors = authors;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public boolean isAvailable() {
        return available;
    }

    public List<AuthorResponse> getAuthors() {
        return authors;
    }
}
