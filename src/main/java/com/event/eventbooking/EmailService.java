package com.event.eventbooking;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY:}")
    private String apiKey;

    @Value("${SENDER_EMAIL:janjam.srivarsha2@gmail.com}")
    private String senderEmail;

    public void sendTicketEmail(Booking booking, String qrBase64) {
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("BREVO_API_KEY is not set. Skipping email.");
            return;
        }

        try {
            String base64Data = qrBase64.contains(",") ? qrBase64.split(",")[1] : qrBase64;

            String jsonPayload = """
            {
              "sender": {"name": "College Events", "email": "%s"},
              "to": [{"email": "%s", "name": "%s"}],
              "subject": "Your Event Ticket - %s",
              "htmlContent": "<p>Hello <b>%s</b>,</p><p>Your registration for <b>%s</b> is confirmed!</p><p>Ticket ID: <b>%s</b></p><p>Please find your QR code attached below.</p>",
              "attachment": [
                {
                  "name": "ticket-qr.png",
                  "content": "%s"
                }
              ]
            }
            """.formatted(
                    senderEmail,
                    booking.getEmail(),
                    booking.getStudentName(),
                    booking.getEventName(),
                    booking.getStudentName(),
                    booking.getEventName(),
                    booking.getTokenId(),
                    base64Data
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Email sent successfully via Brevo API: " + response.body());
            } else {
                System.err.println("Brevo API error: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Failed to send email via API: " + e.getMessage());
        }
    }
}