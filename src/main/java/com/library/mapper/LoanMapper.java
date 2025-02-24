package com.library.mapper;

import com.library.dto.LoanDto;
import com.library.entity.Loan;
import com.library.entity.User;
import com.library.entity.Book;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "book.bookId", target = "bookId")
    LoanDto toDto(Loan loan);

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "bookId", target = "book.bookId")
    Loan toEntity(LoanDto loanDto);

    List<LoanDto> toDtoList(List<Loan> loans);
    List<Loan> toEntityList(List<LoanDto> loanDtos);
}
