package com.project.librarymanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.project.librarymanagement.entity.Book;
import com.project.librarymanagement.entity.BorrowingRecord;
import com.project.librarymanagement.entity.Member;
import com.project.librarymanagement.repository.BookRepository;
import com.project.librarymanagement.repository.BorrowingRecordRepository;
import com.project.librarymanagement.repository.MemberRepository;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BorrowingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Creates a test book in PostgreSQL.
    private Book createBook() {
        Book book = new Book();

        book.setTitle("Integration Test Book");
        book.setIsbn("T" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 12));
        book.setPublishedYear(2024);
        book.setAvailable(true);

        return bookRepository.saveAndFlush(book);
    }

    // Creates a test member in PostgreSQL.
    private Member createMember() {
        Member member = new Member();

        member.setName("Integration Test Member");
        member.setEmail(
                "test-" + UUID.randomUUID() + "@example.com"
        );

        return memberRepository.saveAndFlush(member);
    }

    // TEST 1: Successfully borrow an available book.
    @Test
    void shouldBorrowAvailableBook() throws Exception {

        Book book = createBook();
        Member member = createMember();

        String requestBody = """
                {
                    "bookId": %d,
                    "memberId": %d
                }
                """.formatted(book.getId(), member.getId());

        mockMvc.perform(post("/api/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bookId")
                        .value(book.getId()))
                .andExpect(jsonPath("$.memberId")
                        .value(member.getId()))
                .andExpect(jsonPath("$.dueDate").exists())
                .andExpect(jsonPath("$.returned").value(false));

        Book updatedBook = bookRepository.findById(book.getId())
                .orElseThrow();

        assertFalse(updatedBook.isAvailable());

        assertTrue(
                borrowingRecordRepository
                        .existsByBookIdAndReturnedAtIsNull(book.getId())
        );
    }

    // TEST 2: Reject borrowing a book that is already borrowed.
    @Test
    void shouldRejectBorrowingUnavailableBook() throws Exception {

        Book book = createBook();
        Member member = createMember();

        String requestBody = """
                {
                    "bookId": %d,
                    "memberId": %d
                }
                """.formatted(book.getId(), member.getId());

        // First borrowing should succeed.
        mockMvc.perform(post("/api/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // Second borrowing of the same book should fail.
        mockMvc.perform(post("/api/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // TEST 3: Successfully return a borrowed book.
    @Test
    void shouldReturnBorrowedBook() throws Exception {

        Book book = createBook();
        Member member = createMember();

        String requestBody = """
                {
                    "bookId": %d,
                    "memberId": %d
                }
                """.formatted(book.getId(), member.getId());

        MvcResult borrowResult = mockMvc.perform(
                        post("/api/borrowings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode responseJson = objectMapper.readTree(
                borrowResult.getResponse().getContentAsString()
        );

        long borrowingId = responseJson.get("id").asLong();

        mockMvc.perform(put(
                        "/api/borrowings/{id}/return",
                        borrowingId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returned").value(true))
                .andExpect(jsonPath("$.returnedAt").exists());

        Book updatedBook = bookRepository.findById(book.getId())
                .orElseThrow();

        assertTrue(updatedBook.isAvailable());

        BorrowingRecord record = borrowingRecordRepository
                .findById(borrowingId)
                .orElseThrow();

        assertNotNull(record.getReturnedAt());
    }

    // TEST 4: Verify repository query methods for active borrowings.
    @Test
    void shouldFindActiveBorrowingsUsingRepositoryQueries()
            throws Exception {

        Book book = createBook();
        Member member = createMember();

        String requestBody = """
                {
                    "bookId": %d,
                    "memberId": %d
                }
                """.formatted(book.getId(), member.getId());

        mockMvc.perform(post("/api/borrowings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        assertTrue(
                borrowingRecordRepository
                        .existsByBookIdAndReturnedAtIsNull(book.getId())
        );

        assertEquals(
                1,
                borrowingRecordRepository
                        .findByMemberIdAndReturnedAtIsNull(member.getId())
                        .size()
        );
    }
}
