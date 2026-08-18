package com.example.turf.service;

import com.example.turf.dto.OperatingHoursRequest;
import com.example.turf.dto.OperatingHoursResponse;
import com.example.turf.entity.OperatingHours;
import com.example.turf.repository.OperatingHoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class OperatingHoursService {

    private static final Long SINGLETON_ID = 1L;
    private static final LocalTime DEFAULT_OPEN = LocalTime.of(6, 0);
    private static final LocalTime DEFAULT_CLOSE = LocalTime.of(23, 0);

    private final OperatingHoursRepository operatingHoursRepository;

    public OperatingHoursResponse updateHours(OperatingHoursRequest request) {
        if (!request.getCloseTime().isAfter(request.getOpenTime())) {
            throw new IllegalArgumentException("Close time must be after open time");
        }

        OperatingHours hours = OperatingHours.builder()
                .id(SINGLETON_ID)
                .openTime(request.getOpenTime())
                .closeTime(request.getCloseTime())
                .build();

        return toResponse(operatingHoursRepository.save(hours));
    }

    public OperatingHoursResponse getHours() {
        return toResponse(getOrCreateDefault());
    }

    public OperatingHours getRawHours() {
        return getOrCreateDefault();
    }

    private OperatingHours getOrCreateDefault() {
        return operatingHoursRepository.findById(SINGLETON_ID)
                .orElseGet(() -> operatingHoursRepository.save(
                        OperatingHours.builder()
                                .id(SINGLETON_ID)
                                .openTime(DEFAULT_OPEN)
                                .closeTime(DEFAULT_CLOSE)
                                .build()
                ));
    }

    private OperatingHoursResponse toResponse(OperatingHours hours) {
        return OperatingHoursResponse.builder()
                .openTime(hours.getOpenTime())
                .closeTime(hours.getCloseTime())
                .build();
    }
}