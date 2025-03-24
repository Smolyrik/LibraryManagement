package com.library.controller;

import com.library.dto.AuthorDto;
import com.library.service.impl.AuthorServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@Tag(name = "Author Management", description = "Operations related to authors in the library")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorServiceImpl authorService;

    @Operation(
            summary = "Add a new author",
            description = "Adds a new author to the library.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully added the author",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<AuthorDto> addAuthor(@RequestBody AuthorDto authorDto) {
        return ResponseEntity.ok(authorService.addAuthor(authorDto));
    }

    @Operation(
            summary = "Get author by ID",
            description = "Fetch an author by their unique ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the author",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Author not found")
            })
    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Integer id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @Operation(
            summary = "Get all authors",
            description = "Fetch a list of all authors in the library.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetched the list of authors",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDto.class)))
            })
    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @Operation(
            summary = "Update author",
            description = "Update details of an existing author.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated the author",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorDto.class))),
                    @ApiResponse(responseCode = "404", description = "Author not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            })
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Integer id, @RequestBody AuthorDto authorDto) {
        return ResponseEntity.ok(authorService.updateAuthor(id, authorDto));
    }

    @Operation(
            summary = "Delete author",
            description = "Delete an author by their unique ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted the author"),
                    @ApiResponse(responseCode = "404", description = "Author not found")
            })
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Integer id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
