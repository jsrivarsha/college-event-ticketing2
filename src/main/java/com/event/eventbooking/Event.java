package com.event.eventbooking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int totalSlots;

    @Column(nullable = false)
    private int bookedSlots;

    public Event() {}

    public Event(String name, int totalSlots) {
        this.name = name;
        this.totalSlots = totalSlots;
        this.bookedSlots = 0;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getTotalSlots() { return totalSlots; }
    public void setTotalSlots(int totalSlots) { this.totalSlots = totalSlots; }
    public int getBookedSlots() { return bookedSlots; }
    public void setBookedSlots(int bookedSlots) { this.bookedSlots = bookedSlots; }

    public int getAvailableSlots() {
        return totalSlots - bookedSlots;
    }
}

