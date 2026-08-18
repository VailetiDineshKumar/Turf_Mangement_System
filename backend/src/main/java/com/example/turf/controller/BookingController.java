package com.example.turf.controller;

import com.example.turf.dto.BookingRequest;
import com.example.turf.dto.BookingResponse;
import com.example.turf.entity.User;
import com.example.turf.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/api/bookings")
    public ResponseEntity<List<BookingResponse>> bookSlots(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseEntity.ok(bookingService.bookSlots(currentUser, request.getSlotIds()));
    }

    @DeleteMapping("/api/bookings/group/{groupId}")
    public ResponseEntity<Void> cancelBookingGroup(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String groupId
    ) {
        bookingService.cancelBookingGroup(currentUser, groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/bookings/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(bookingService.getMyBookings(currentUser.getId()));
    }

    @GetMapping("/api/admin/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }
}