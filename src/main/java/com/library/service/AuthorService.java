package com.library.service;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.mapper.AuthorMapper;
import com.library.repository.AuthorRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Transactional
    public AuthorDto addAuthor(AuthorDto authorDto) {
        Author author = authorMapper.toEntity(authorDto);
        Author savedAuthor = authorRepository.save(author);
        log.info("Added new author with ID: {}", savedAuthor.getAuthorId());
        return authorMapper.toDto(savedAuthor);
    }

    public AuthorDto getAuthorById(Integer authorId) {
        return authorRepository.findById(authorId)
                .map(authorMapper::toDto)
                .orElseThrow(() -> {
                    log.error("Author with ID: {} not found", authorId);
                    return new NoSuchElementException("Author with ID: " + authorId + " not found");
                });
    }

    public List<AuthorDto> getAllAuthors() {
        log.info("Fetching all authors");
        return authorRepository.findAll().stream()
                .map(authorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AuthorDto updateAuthor(Integer authorId, AuthorDto authorDto) {
        if (!authorRepository.existsById(authorId)) {
            log.error("Author with ID: {} not found", authorId);
            throw new NoSuchElementException("Author with ID: " + authorId + " not found");
        }

        Author updatedAuthor = authorMapper.toEntity(authorDto);
        updatedAuthor.setAuthorId(authorId);

        Author savedAuthor = authorRepository.save(updatedAuthor);
        log.info("Updated author with ID: {}", savedAuthor.getAuthorId());

        return authorMapper.toDto(savedAuthor);
    }

    @Transactional
    public void deleteAuthor(Integer authorId) {
        if (!authorRepository.existsById(authorId)) {
            log.error("Author with ID: {} not found", authorId);
            throw new NoSuchElementException("Author with ID: " + authorId + " not found");
        }
        authorRepository.deleteById(authorId);
        log.info("Deleted author with ID: {}", authorId);
    }
}
