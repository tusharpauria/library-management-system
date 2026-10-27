package com.project.librarymanagement.repository;

import com.project.librarymanagement.entity.BorrowingRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    List<BorrowingRecord> findByMemberId(Long memberId);

    List<BorrowingRecord> findByMemberIdAndReturnedAtIsNull(Long memberId);

    Optional<BorrowingRecord> findByIdAndReturnedAtIsNull(Long id);

    boolean existsByBookIdAndReturnedAtIsNull(Long bookId);

    boolean existsByBookId(Long id);

    boolean existsByMemberId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT br FROM BorrowingRecord br WHERE br.id = :id")
    Optional<BorrowingRecord> findByIdForUpdate(@Param("id") Long id);

}
