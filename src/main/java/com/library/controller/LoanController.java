package com.library.controller;

import com.library.dto.LoanDto;
import com.library.service.impl.LoanServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loan Management", description = "Operations related to book loans")
public class LoanController {

    private final LoanServiceImpl loanService;

    @Operation(
            summary = "Add a new loan",
            description = "Creates a new loan record.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created loan",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoanDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<LoanDto> addLoan(@Valid @RequestBody LoanDto loanDto) {
        return ResponseEntity.ok(loanService.addLoan(loanDto));
    }

    @Operation(
            summary = "Get loan by ID",
            description = "Retrieves a loan by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved loan",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoanDto.class))),
                    @ApiResponse(responseCode = "404", description = "Loan not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<LoanDto> getLoanById(@PathVariable Integer id) {
        return ResponseEntity.ok(loanService.getLoanById(id));
    }

    @Operation(
            summary = "Get all loans",
            description = "Retrieves a list of all loans.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of loans",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LoanDto.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @Operation(
            summary = "Update loan",
            description = "Updates an existing loan record.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated loan",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoanDto.class))),
                    @ApiResponse(responseCode = "404", description = "Loan not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<LoanDto> updateLoan(@PathVariable Integer id, @Valid @RequestBody LoanDto loanDto) {
        return ResponseEntity.ok(loanService.updateLoan(id, loanDto));
    }

    @Operation(
            summary = "Delete loan",
            description = "Removes a loan record by its ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted loan"),
                    @ApiResponse(responseCode = "404", description = "Loan not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLoan(@PathVariable Integer id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Loan a book",
            description = "Registers a loan for a book to a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully loaned book",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LoanDto.class)))
            }
    )
    @PostMapping("/loan")
    public ResponseEntity<LoanDto> loanBook(@RequestParam Integer userId, @RequestParam Integer bookId, @RequestParam Integer loanTime) {
        return ResponseEntity.ok(loanService.loanBook(userId, bookId, loanTime));
    }

    @Operation(
            summary = "Return a book",
            description = "Marks a book loan as returned.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully returned book")
            }
    )
    @PostMapping("/return/{id}")
    public ResponseEntity<Boolean> returnBook(@PathVariable Integer id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }

    @Operation(
            summary = "Get loans by user",
            description = "Retrieves all loans associated with a user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user loans",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LoanDto.class))))
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LoanDto>> getLoansByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(loanService.getLoansByUserId(userId));
    }

    @Operation(
            summary = "Get overdue loans",
            description = "Retrieves all overdue loans.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved overdue loans",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LoanDto.class))))
            }
    )
    @GetMapping("/overdue")
    public ResponseEntity<List<LoanDto>> getOverdueLoans() {
        return ResponseEntity.ok(loanService.checkOverdueLoans());
    }
}
