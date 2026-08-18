package com.example.turf.repository;

import com.example.turf.entity.Slot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    List<Slot> findByDate(LocalDate date);

    List<Slot> findByBookedFalse();

    boolean existsByDateAndStartTime(LocalDate date, LocalTime startTime);
    /**
     * Locks the slot row for the duration of the transaction (SELECT ... FOR UPDATE).
     * Used by BookingService when creating a booking so two concurrent requests
     * for the same slot can't both succeed — the second request blocks until the
     * first transaction commits/rolls back, then re-checks availability.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Slot s where s.id = :id")
    Optional<Slot> findByIdForUpdate(Long id);
}