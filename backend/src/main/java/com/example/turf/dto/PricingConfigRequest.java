package com.example.turf.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PricingConfigRequest {

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be positive")
    private BigDecimal basePrice;

    @NotNull(message = "Weekend price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Weekend price must be positive")
    private BigDecimal weekendPrice;
}