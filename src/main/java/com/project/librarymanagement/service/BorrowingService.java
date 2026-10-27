package com.project.librarymanagement.service;

import com.project.librarymanagement.dto.BorrowingRequest;
import com.project.librarymanagement.dto.BorrowingResponse;
import com.project.librarymanagement.entity.Book;
import com.project.librarymanagement.entity.BorrowingRecord;
import com.project.librarymanagement.entity.Member;
import com.project.librarymanagement.repository.BookRepository;
import com.project.librarymanagement.repository.BorrowingRecordRepository;
import com.project.librarymanagement.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class BorrowingService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    public BorrowingService(
            BookRepository bookRepository,
            MemberRepository memberRepository,
            BorrowingRecordRepository borrowingRecordRepository) {

        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    // BORROW A BOOK
    @Transactional
    public BorrowingResponse borrowBook(BorrowingRequest request) {

        // Lock the book while checking and updating its availability.
        Book book = bookRepository.findByIdForUpdate(request.getBookId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found with id: " + request.getBookId()
                ));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Member not found with id: " + request.getMemberId()
                ));

        if (!book.isAvailable() || borrowingRecordRepository
                .existsByBookIdAndReturnedAtIsNull(book.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book is not available for borrowing"
            );
        }

        LocalDate borrowedAt = LocalDate.now();
        LocalDate dueDate = borrowedAt.plusDays(14);

        BorrowingRecord record = new BorrowingRecord();
        record.setBook(book);
        record.setMember(member);
        record.setBorrowedAt(borrowedAt);
        record.setDueDate(dueDate);
        record.setReturnedAt(null);

        book.setAvailable(false);

        bookRepository.save(book);

        BorrowingRecord savedRecord = borrowingRecordRepository.save(record);

        return toResponse(savedRecord);

    }

    // RETURN A BOOK
    @Transactional
    public BorrowingResponse returnBook(Long borrowingId) {

        BorrowingRecord record = borrowingRecordRepository.findByIdForUpdate(borrowingId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Borrowing record not found with id: "
                                        + borrowingId
                        ));

        if (record.getReturnedAt() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This book has already been returned"
            );
        }

        Book book = bookRepository.findByIdForUpdate(
                        record.getBook().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found"
                ));

        record.setReturnedAt(LocalDate.now());
        book.setAvailable(true);

        BorrowingRecord savedRecord = borrowingRecordRepository.save(record);

        bookRepository.save(book);

        return toResponse(savedRecord);
    }

    // GET BORROWING RECORD BY ID
    @Transactional(readOnly = true)
    public BorrowingResponse getBorrowingById(Long id) {

        BorrowingRecord record =
                borrowingRecordRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Borrowing record not found with id: " + id
                        ));

        return toResponse(record);
    }

    // GET ALL BORROWING RECORDS
    @Transactional(readOnly = true)
    public Page<BorrowingResponse> getAllBorrowings(Pageable pageable) {

        return borrowingRecordRepository.findAll(pageable)
                .map(this::toResponse);
    }

    private BorrowingResponse toResponse(BorrowingRecord record) {

        return new BorrowingResponse(
                record.getId(),
                record.getBook().getId(),
                record.getBook().getTitle(),
                record.getMember().getId(),
                record.getMember().getName(),
                record.getBorrowedAt(),
                record.getDueDate(),
                record.getReturnedAt()
        );
    }
}