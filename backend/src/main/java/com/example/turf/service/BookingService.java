package com.example.turf.service;

import com.example.turf.entity.Booking;
import com.example.turf.entity.Slot;
import com.example.turf.entity.User;
import com.example.turf.enums.BookingStatus;
import com.example.turf.enums.Role;
import com.example.turf.dto.BookingResponse;
import com.example.turf.repository.BookingRepository;
import com.example.turf.repository.SlotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SlotRepository slotRepository;

    /**
     * Books one or more consecutive slots as a single reservation (e.g. a
     * 2-hour booking = two consecutive 1-hour Slot rows). All slots are locked
     * (pessimistic write) in ascending-ID order, in this one transaction —
     * locking in a fixed order across all callers is what prevents deadlocks
     * when two overlapping multi-slot bookings are attempted concurrently.
     */
    @Transactional
    public List<BookingResponse> bookSlots(User user, List<Long> slotIds) {
        if (slotIds == null || slotIds.isEmpty()) {
            throw new IllegalArgumentException("At least one slot must be selected");
        }

        List<Long> orderedIds = slotIds.stream().distinct().sorted().toList();

        List<Slot> slots = new ArrayList<>();
        for (Long id : orderedIds) {
            Slot slot = slotRepository.findByIdForUpdate(id)
                    .orElseThrow(() -> new EntityNotFoundException("Slot not found with id: " + id));
            slots.add(slot);
        }

        // Re-sort chronologically to validate the booking makes sense as a
        // contiguous time block, regardless of the ID locking order above.
        slots.sort(Comparator.comparing(Slot::getStartTime));

        for (Slot slot : slots) {
            if (slot.isBooked()) {
                throw new IllegalArgumentException("One or more selected slots are already booked");
            }
        }

        LocalDate date = slots.get(0).getDate();
        for (int i = 0; i < slots.size(); i++) {
            if (!slots.get(i).getDate().equals(date)) {
                throw new IllegalArgumentException("Selected slots must all be on the same date");
            }
            if (i > 0 && !slots.get(i).getStartTime().equals(slots.get(i - 1).getEndTime())) {
                throw new IllegalArgumentException("Selected slots must be consecutive with no gaps");
            }
        }

        LocalDateTime firstSlotStart = LocalDateTime.of(slots.get(0).getDate(), slots.get(0).getStartTime());
        if (firstSlotStart.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book a slot in the past");
        }

        String groupId = UUID.randomUUID().toString();
        List<Booking> created = new ArrayList<>();

        for (Slot slot : slots) {
            slot.setBooked(true);
            slotRepository.save(slot);

            Booking booking = Booking.builder()
                    .user(user)
                    .slot(slot)
                    .status(BookingStatus.CONFIRMED)
                    .groupId(groupId)
                    .build();
            created.add(bookingRepository.save(booking));
        }

        return created.stream().map(this::toResponse).toList();
    }

    @Transactional
    public void cancelBookingGroup(User currentUser, String groupId) {
        List<Booking> bookings = bookingRepository.findByGroupId(groupId);
        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("Booking not found");
        }

        Booking first = bookings.get(0);
        boolean isOwner = first.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new IllegalArgumentException("You are not authorized to cancel this booking");
        }

        for (Booking booking : bookings) {
            if (booking.getStatus() == BookingStatus.CANCELLED) continue;

            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);

            Slot slot = booking.getSlot();
            slot.setBooked(false);
            slotRepository.save(slot);
        }
    }

    public List<BookingResponse> getMyBookings(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllWithUserAndSlot().stream()
                .map(this::toResponseWithUser)
                .toList();
    }

    private BookingResponse toResponse(Booking booking) {
        Slot slot = booking.getSlot();
        return BookingResponse.builder()
                .id(booking.getId())
                .groupId(booking.getGroupId())
                .slotId(slot.getId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .price(slot.getPrice())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .userEmail(null)
                .userName(null)
                .build();
    }

    private BookingResponse toResponseWithUser(Booking booking) {
        Slot slot = booking.getSlot();
        User user = booking.getUser();
        return BookingResponse.builder()
                .id(booking.getId())
                .groupId(booking.getGroupId())
                .slotId(slot.getId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .price(slot.getPrice())
                .status(booking.getStatus())
                .bookedAt(booking.getBookedAt())
                .userEmail(user.getEmail())
                .userName(user.getName())
                .build();
    }
}