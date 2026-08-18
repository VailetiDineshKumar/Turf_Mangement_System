package com.example.turf.controller;

import com.example.turf.dto.OperatingHoursRequest;
import com.example.turf.dto.OperatingHoursResponse;
import com.example.turf.service.OperatingHoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class OperatingHoursController {

    private final OperatingHoursService operatingHoursService;

    @PutMapping("/api/admin/operating-hours")
    public ResponseEntity<OperatingHoursResponse> updateHours(@Valid @RequestBody OperatingHoursRequest request) {
        return ResponseEntity.ok(operatingHoursService.updateHours(request));
    }

    @GetMapping("/api/operating-hours")
    public ResponseEntity<OperatingHoursResponse> getHours() {
        return ResponseEntity.ok(operatingHoursService.getHours());
    }
}