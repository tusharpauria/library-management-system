package com.project.librarymanagement.repository;

import com.project.librarymanagement.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    List<BorrowingRecord> findByMemberId(Long memberId);

    List<BorrowingRecord> findByMemberIdAndReturnedAtIsNull(Long memberId);

    Optional<BorrowingRecord> findByIdAndReturnedAtIsNull(Long id);

    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);

    boolean existsByBookId(Long id);

}
