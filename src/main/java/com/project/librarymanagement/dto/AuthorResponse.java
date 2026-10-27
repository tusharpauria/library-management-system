package com.project.librarymanagement.dto;

public class AuthorResponse {

    private Long id;
    private String name;
    private String biography;

    public AuthorResponse() {
    }

    public AuthorResponse(Long id, String name, String biography) {
        this.id = id;
        this.name = name;
        this.biography = biography;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBiography() {
        return biography;
    }
}
