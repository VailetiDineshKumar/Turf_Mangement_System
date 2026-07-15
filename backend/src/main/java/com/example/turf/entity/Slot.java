package com.example.turf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Slot entity — a fixed time block on a given date that can be booked.
 * Managed (create/edit/delete/pricing) exclusively by Admin.
 */
@Entity
@Table(name = "slots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Slot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // Denormalized convenience flag; source of truth for "is this bookable right now"
    // is still enforced via a pessimistic lock in BookingService, not just this flag.
    @Column(name = "is_booked", nullable = false)
    @Builder.Default
    private boolean booked = false;
}