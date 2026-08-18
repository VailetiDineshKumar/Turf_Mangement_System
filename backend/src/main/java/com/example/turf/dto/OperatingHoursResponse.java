package com.example.turf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class OperatingHoursResponse {
    private LocalTime openTime;
    private LocalTime closeTime;
}