package com.event.eventbooking;

import java.util.Base64;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTicketEmail(Booking booking, String qrBase64) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Dynamically sends to whoever filled the form:
            helper.setTo(booking.getEmail());
            helper.setSubject("🎟 Ticket Confirmation: " + booking.getEventName());

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 500px; margin: auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 12px;">
                    <h2 style="color: #2563eb; text-align: center; margin-bottom: 4px;">%s</h2>
                    <p style="text-align: center; color: #64748b; margin-top: 0; font-size: 13px; font-weight: bold;">ADMISSION PASS</p>
                    <hr style="border: none; border-top: 1px dashed #cbd5e1; margin: 15px 0;">
                    <p><strong>Attendee:</strong> %s</p>
                    <p><strong>Register No:</strong> %s</p>
                    <p><strong>Phone:</strong> %s</p>
                    <p><strong>Token ID:</strong> <span style="font-family: monospace; color: #2563eb; font-weight: bold;">%s</span></p>
                    <hr style="border: none; border-top: 1px dashed #cbd5e1; margin: 15px 0;">
                    <div style="text-align: center; margin-top: 15px;">
                        <p style="font-size: 13px; color: #64748b;">Show this QR pass at the entrance gate:</p>
                        <img src="cid:qrCodeImage" style="width: 220px; height: 220px; border-radius: 8px; border: 1px solid #eee;" alt="Ticket QR Code"/>
                    </div>
                </div>
                """.formatted(booking.getEventName(), booking.getStudentName(), booking.getRegisterNo(), booking.getPhoneNumber(), booking.getTokenId());

            helper.setText(htmlContent, true);

            // Attach QR Code as an inline image
            byte[] imageBytes = Base64.getDecoder().decode(qrBase64);
            helper.addInline("qrCodeImage", new ByteArrayResource(imageBytes), "image/png");

            mailSender.send(message);
            System.out.println(">>> Ticket email successfully sent to: " + booking.getEmail() + " <<<");
        } catch (MessagingException e) {
            System.err.println("Failed to send email to " + booking.getEmail() + ": " + e.getMessage());
        }
    }
}