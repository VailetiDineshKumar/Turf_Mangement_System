package com.example.turf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PricingConfigResponse {
    private BigDecimal basePrice;
    private BigDecimal weekendPrice;
}