package com.project.librarymanagement.dto;

import java.time.LocalDate;

public class BorrowingResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long memberId;
    private String memberName;
    private LocalDate borrowedAt;
    private LocalDate dueDate;
    private LocalDate returnedAt;
    private boolean returned;

    public BorrowingResponse() {
    }

    public BorrowingResponse(
            Long id,
            Long bookId,
            String bookTitle,
            Long memberId,
            String memberName,
            LocalDate borrowedAt,
            LocalDate dueDate,
            LocalDate returnedAt) {

        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberName = memberName;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
        this.returnedAt = returnedAt;
        this.returned = returnedAt != null;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public LocalDate getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedAt() {
        return returnedAt;
    }

    public boolean isReturned() {
        return returned;
    }
}
