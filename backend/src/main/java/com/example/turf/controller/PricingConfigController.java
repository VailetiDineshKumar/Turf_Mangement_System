package com.example.turf.controller;

import com.example.turf.dto.PricingConfigRequest;
import com.example.turf.dto.PricingConfigResponse;
import com.example.turf.service.PricingConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PricingConfigController {

    private final PricingConfigService pricingConfigService;

    @PutMapping("/api/admin/pricing-config")
    public ResponseEntity<PricingConfigResponse> updateConfig(@Valid @RequestBody PricingConfigRequest request) {
        return ResponseEntity.ok(pricingConfigService.updateConfig(request));
    }

    @GetMapping("/api/pricing-config")
    public ResponseEntity<PricingConfigResponse> getConfig() {
        return ResponseEntity.ok(pricingConfigService.getConfig());
    }
}