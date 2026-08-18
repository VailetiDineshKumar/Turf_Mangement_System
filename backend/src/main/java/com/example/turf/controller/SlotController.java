package com.example.turf.controller;

import com.example.turf.dto.GenerateSlotsRequest;
import com.example.turf.dto.SlotRequest;
import com.example.turf.dto.SlotResponse;
import com.example.turf.service.SlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.turf.dto.GenerateSlotsRequest;
import java.time.LocalDate;
import java.util.List;

// 
@RestController
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping("/api/admin/slots")
    public ResponseEntity<SlotResponse> createSlot(@Valid @RequestBody SlotRequest request) {
        return ResponseEntity.ok(slotService.createSlot(request));
    }

    @PutMapping("/api/admin/slots/{id}")
    public ResponseEntity<SlotResponse> updateSlot(@PathVariable Long id, @Valid @RequestBody SlotRequest request) {
        return ResponseEntity.ok(slotService.updateSlot(id, request));
    }

    @DeleteMapping("/api/admin/slots/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        slotService.deleteSlot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/slots")
    public ResponseEntity<List<SlotResponse>> getAllSlots() {
        return ResponseEntity.ok(slotService.getAllSlots());
    }

    @GetMapping("/api/slots")
    public ResponseEntity<List<SlotResponse>> getAvailableSlots(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date != null) {
            return ResponseEntity.ok(slotService.getSlotsByDate(date));
        }
        return ResponseEntity.ok(slotService.getAvailableSlots());
    }

    @PostMapping("/api/admin/slots/generate")
public ResponseEntity<List<SlotResponse>> generateSlots(@Valid @RequestBody GenerateSlotsRequest request) {
    return ResponseEntity.ok(slotService.generateSlots(request.getStartDate(), request.getEndDate()));
}
}