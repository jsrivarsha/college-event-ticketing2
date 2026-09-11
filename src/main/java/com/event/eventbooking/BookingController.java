package com.event.eventbooking;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingController {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    public BookingController(EventRepository eventRepository,
                             BookingRepository bookingRepository,
                             QRCodeService qrCodeService,
                             EmailService emailService) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String showBookingPage(Model model) {
        model.addAttribute("events", eventRepository.findAll());
        return "index";
    }

    @PostMapping("/book")
    public String handleBooking(@RequestParam String studentName,
                                @RequestParam String email,
                                @RequestParam String registerNo,
                                @RequestParam String phoneNumber,
                                @RequestParam String eventName,
                                Model model) {

        if (bookingRepository.existsByRegisterNoAndEventName(registerNo, eventName)) {
            model.addAttribute("errorMessage", "You have already registered for this event!");
            model.addAttribute("events", eventRepository.findAll());
            return "index";
        }

        Optional<Event> eventOpt = eventRepository.findByName(eventName);
        if (eventOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Selected event does not exist.");
            model.addAttribute("events", eventRepository.findAll());
            return "index";
        }

        Event event = eventOpt.get();

        if (event.getAvailableSlots() <= 0) {
            model.addAttribute("errorMessage", "Sorry, tickets for this event are completely sold out!");
            model.addAttribute("events", eventRepository.findAll());
            return "index";
        }

        event.setBookedSlots(event.getBookedSlots() + 1);
        eventRepository.save(event);

        String tokenId = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Booking booking = new Booking();
        booking.setStudentName(studentName);
        booking.setEmail(email);
        booking.setRegisterNo(registerNo);
        booking.setPhoneNumber(phoneNumber);
        booking.setEventName(eventName);
        booking.setTokenId(tokenId);
        bookingRepository.save(booking);

        String qrData = "Token: " + tokenId + "\nEvent: " + eventName + "\nStudent: " + studentName + " (" + registerNo + ")";
        String qrBase64 = qrCodeService.generateQRCodeBase64(qrData, 250, 250);

        // Send confirmation email to attendee
        emailService.sendTicketEmail(booking, qrBase64);

        model.addAttribute("booking", booking);
        model.addAttribute("qrBase64", qrBase64);
        return "ticket";
    }
}