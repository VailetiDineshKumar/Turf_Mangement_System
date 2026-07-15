package com.example.turf.repository;

import com.example.turf.entity.Booking;
import com.example.turf.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByStatus(BookingStatus status);

    boolean existsBySlotIdAndStatus(Long slotId, BookingStatus status);
}