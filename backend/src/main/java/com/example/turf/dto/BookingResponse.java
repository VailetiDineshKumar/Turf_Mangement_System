package com.example.turf.dto;

import com.example.turf.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private String groupId;
    private Long slotId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal price;
    private BookingStatus status;
    private LocalDateTime bookedAt;
    private String userEmail;
    private String userName;
}