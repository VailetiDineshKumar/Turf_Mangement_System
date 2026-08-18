package com.example.turf.service;

import com.example.turf.dto.PricingConfigRequest;
import com.example.turf.dto.PricingConfigResponse;
import com.example.turf.entity.PricingConfig;
import com.example.turf.repository.PricingConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PricingConfigService {

    private static final Long SINGLETON_ID = 1L;
    private static final BigDecimal DEFAULT_BASE_PRICE = BigDecimal.valueOf(500);
    private static final BigDecimal DEFAULT_WEEKEND_PRICE = BigDecimal.valueOf(700);

    private final PricingConfigRepository pricingConfigRepository;

    public PricingConfigResponse updateConfig(PricingConfigRequest request) {
        PricingConfig config = PricingConfig.builder()
                .id(SINGLETON_ID)
                .basePrice(request.getBasePrice())
                .weekendPrice(request.getWeekendPrice())
                .build();

        PricingConfig saved = pricingConfigRepository.save(config);
        return toResponse(saved);
    }

    public PricingConfigResponse getConfig() {
        return toResponse(getOrCreateDefault());
    }

    public BigDecimal calculatePriceForDate(LocalDate date) {
        PricingConfig config = getOrCreateDefault();
        boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        return isWeekend ? config.getWeekendPrice() : config.getBasePrice();
    }

    private PricingConfig getOrCreateDefault() {
        return pricingConfigRepository.findById(SINGLETON_ID)
                .orElseGet(() -> pricingConfigRepository.save(
                        PricingConfig.builder()
                                .id(SINGLETON_ID)
                                .basePrice(DEFAULT_BASE_PRICE)
                                .weekendPrice(DEFAULT_WEEKEND_PRICE)
                                .build()
                ));
    }

    private PricingConfigResponse toResponse(PricingConfig config) {
        return PricingConfigResponse.builder()
                .basePrice(config.getBasePrice())
                .weekendPrice(config.getWeekendPrice())
                .build();
    }
}