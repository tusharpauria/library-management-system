package com.project.librarymanagement.repository;
import com.project.librarymanagement.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}
