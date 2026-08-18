package com.example.turf.repository;

import com.example.turf.entity.Booking;
import com.example.turf.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByGroupId(String groupId);

    List<Booking> findByStatus(BookingStatus status);

    boolean existsBySlotIdAndStatus(Long slotId, BookingStatus status);

    @Query("select b from Booking b join fetch b.user join fetch b.slot order by b.bookedAt desc")
    List<Booking> findAllWithUserAndSlot();
}