package com.example.turf.service;

import com.example.turf.dto.SlotRequest;
import com.example.turf.dto.SlotResponse;
import com.example.turf.entity.OperatingHours;
import com.example.turf.entity.Slot;
import com.example.turf.repository.SlotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final SlotRepository slotRepository;
    private final PricingConfigService pricingConfigService;
    private final OperatingHoursService operatingHoursService;

    public SlotResponse createSlot(SlotRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        BigDecimal price = request.getPrice() != null
                ? request.getPrice()
                : pricingConfigService.calculatePriceForDate(request.getDate());

        Slot slot = Slot.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .price(price)
                .booked(false)
                .build();

        return toResponse(slotRepository.save(slot));
    }

    public SlotResponse updateSlot(Long id, SlotRequest request) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found with id: " + id));

        if (slot.isBooked()) {
            throw new IllegalArgumentException("Cannot edit a slot that is already booked");
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        BigDecimal price = request.getPrice() != null
                ? request.getPrice()
                : pricingConfigService.calculatePriceForDate(request.getDate());

        slot.setDate(request.getDate());
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());
        slot.setPrice(price);

        return toResponse(slotRepository.save(slot));
    }

    public void deleteSlot(Long id) {
        Slot slot = slotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Slot not found with id: " + id));

        if (slot.isBooked()) {
            throw new IllegalArgumentException("Cannot delete a slot that is already booked");
        }

        slotRepository.delete(slot);
    }

    /**
     * Auto-generates 1-hour slots for every day in [startDate, endDate], across the
     * admin-configured operating hours. Skips any slot that already exists for a
     * given date+startTime, so calling this repeatedly (e.g. extending the range
     * later) never creates duplicates.
     */
    public List<SlotResponse> generateSlots(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        OperatingHours hours = operatingHoursService.getRawHours();
        List<Slot> createdSlots = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalTime slotStart = hours.getOpenTime();

            while (true) {
                LocalTime slotEnd = slotStart.plusHours(1);

                // Stop if this hour would run past closing time.
                // (slotEnd.equals(MIDNIGHT) means it wrapped past midnight — treat as end of day)
                boolean wrappedPastMidnight = slotEnd.equals(LocalTime.MIDNIGHT);
                boolean pastClosing = !wrappedPastMidnight && slotEnd.isAfter(hours.getCloseTime());

                if (pastClosing) break;

                final LocalDate currentDate = date;
                final LocalTime currentStart = slotStart;

                boolean alreadyExists = slotRepository.existsByDateAndStartTime(currentDate, currentStart);

                if (!alreadyExists) {
                    BigDecimal price = pricingConfigService.calculatePriceForDate(currentDate);
                    Slot slot = Slot.builder()
                            .date(currentDate)
                            .startTime(currentStart)
                            .endTime(slotEnd)
                            .price(price)
                            .booked(false)
                            .build();
                    createdSlots.add(slotRepository.save(slot));
                }

                if (wrappedPastMidnight) break;
                slotStart = slotEnd;
            }
        }

        return createdSlots.stream().map(this::toResponse).toList();
    }

    public List<SlotResponse> getAllSlots() {
        return slotRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SlotResponse> getAvailableSlots() {
        return slotRepository.findByBookedFalse().stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SlotResponse> getSlotsByDate(LocalDate date) {
        return slotRepository.findByDate(date).stream()
                .map(this::toResponse)
                .toList();
    }

    private SlotResponse toResponse(Slot slot) {
        return SlotResponse.builder()
                .id(slot.getId())
                .date(slot.getDate())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .price(slot.getPrice())
                .booked(slot.isBooked())
                .build();
    }
}