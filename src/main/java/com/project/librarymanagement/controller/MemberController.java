package com.project.librarymanagement.controller;

import com.project.librarymanagement.dto.MemberRequest;
import com.project.librarymanagement.dto.MemberResponse;
import com.project.librarymanagement.entity.Member;
import com.project.librarymanagement.repository.BorrowingRecordRepository;
import com.project.librarymanagement.repository.MemberRepository;
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
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;

    public MemberController(
            MemberRepository memberRepository,
            BorrowingRecordRepository borrowingRecordRepository) {
        this.memberRepository = memberRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
    }

    // CREATE MEMBER
    @PostMapping
    public ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody MemberRequest request) {

        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A member with this email already exists"
            );
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setEmail(request.getEmail());

        Member savedMember = memberRepository.save(member);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(savedMember));
    }

    // GET ALL MEMBERS WITH PAGINATION AND SORTING
    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be >= 0 and size must be between 1 and 100"
            );
        }

        Set<String> allowedSortFields = Set.of("id", "name", "email");

        if (!allowedSortFields.contains(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "sortBy must be id, name, or email"
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

        Page<MemberResponse> members = memberRepository.findAll(pageable)
                .map(this::toResponse);

        return ResponseEntity.ok(members);
    }

    // GET MEMBER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(
            @PathVariable Long id) {

        Member member = findMember(id);

        return ResponseEntity.ok(toResponse(member));
    }

    // UPDATE MEMBER
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {

        Member member = findMember(id);

        memberRepository.findAll().stream()
                .filter(existing -> existing.getEmail()
                        .equalsIgnoreCase(request.getEmail()))
                .filter(existing -> !existing.getId().equals(id))
                .findFirst()
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Another member already uses this email"
                    );
                });

        member.setName(request.getName());
        member.setEmail(request.getEmail());

        Member updatedMember = memberRepository.save(member);

        return ResponseEntity.ok(toResponse(updatedMember));
    }

    // DELETE MEMBER
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {

        Member member = findMember(id);

        if (borrowingRecordRepository.existsByMemberId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a member with borrowing history"
            );
        }

        memberRepository.delete(member);

        return ResponseEntity.noContent().build();
    }

    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Member not found with id: " + id
                ));
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail()
        );
    }
}
