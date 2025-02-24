package com.library.mapper;

import com.library.dto.ReservationDto;
import com.library.entity.Reservation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "book.bookId", target = "bookId")
    ReservationDto toDto(Reservation reservation);

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "bookId", target = "book.bookId")
    Reservation toEntity(ReservationDto reservationDto);

    List<ReservationDto> toDtoList(List<Reservation> reservations);
    List<Reservation> toEntityList(List<ReservationDto> reservationDtos);
}
