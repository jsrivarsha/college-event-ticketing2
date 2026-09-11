package com.event.eventbooking;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByRegisterNoAndEventName(String registerNo, String eventName);
}