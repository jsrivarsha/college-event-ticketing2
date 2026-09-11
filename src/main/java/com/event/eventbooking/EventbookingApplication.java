package com.event.eventbooking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventbookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventbookingApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(EventRepository eventRepository) {
        return args -> {
            if (eventRepository.count() == 0) {
                Event event1 = new Event();
                event1.setName("AI & Robotics Hackathon");
                event1.setTotalSlots(50);
                event1.setBookedSlots(0);
                eventRepository.save(event1);

                Event event2 = new Event();
                event2.setName("Annual Cultural Fest 2026");
                event2.setTotalSlots(100);
                event2.setBookedSlots(0);
                eventRepository.save(event2);

                Event event3 = new Event();
                event3.setName("Cloud Computing Workshop");
                event3.setTotalSlots(30);
                event3.setBookedSlots(0);
                eventRepository.save(event3);

                System.out.println(">>> Sample events successfully inserted into MySQL! <<<");
            }
        };
    }
}