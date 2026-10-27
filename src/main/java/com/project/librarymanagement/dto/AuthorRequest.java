package com.project.librarymanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthorRequest {

    @NotBlank(message = "Author name is required")
    @Size(max = 150, message = "Author name cannot exceed 150 characters")
    private String name;

    private String biography;

    public AuthorRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}