package com.example.turf.entity;

import com.example.turf.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Booking entity — links a User to a Slot.
 * Double-booking prevention is enforced in the service layer via a pessimistic
 * lock on the Slot row during the booking transaction (see BookingService),
 * since "only one CONFIRMED booking per slot" is a conditional constraint that
 * a plain DB unique constraint can't express (a slot can have many CANCELLED
 * bookings but at most one CONFIRMED one).
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "booked_at", updatable = false)
    private LocalDateTime bookedAt;

    @PrePersist
    protected void onCreate() {
        this.bookedAt = LocalDateTime.now();
    }
}