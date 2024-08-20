package com.VEC.CGPA.CALCULATOR.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String toEmail) {
        String subject = "Welcome to the VEC CGPA Calculator";
        String body = "<html><body>" +
                "<h1 style='font-size:20px;'>Welcome to the VEC CGPA Calculator!</h1>" +
                "<p style='font-size:16px;'>Thank you for signing up! We are excited to have you on board.</p>" +
                "<p style='font-size:16px;'>With the VEC CGPA Calculator, you can easily:</p>" +
                "<ul style='font-size:16px;'>" +
                "<li>Calculate your CGPA accurately.</li>" +
                "<li>Track your academic progress over time.</li>" +
                "<li>Receive a detailed report of your CGPA via email.</li>" +
                "<li>Get personalized tips to improve your academic performance.</li>" +
                "</ul>" +
                "<p style='font-size:16px;'>We hope you find our service helpful and wish you the best in your academic journey!</p>" +
                "</body></html>";

        sendHtmlMail(toEmail, subject, body);
    }

    public void sendLoginEmail(String toEmail) {
        String subject = "Welcome Back to the VEC CGPA Calculator";
        String body = "<html><body>" +
                "<h1 style='font-size:20px;'>Welcome Back!</h1>" +
                "<p style='font-size:16px;'>We're glad to see you again at the VEC CGPA Calculator.</p>" +
                "<p style='font-size:16px;'>Remember, with our service you can:</p>" +
                "<ul style='font-size:16px;'>" +
                "<li>Calculate your CGPA quickly and accurately.</li>" +
                "<li>Access detailed reports of your academic performance.</li>" +
                "<li>Receive tips and advice to help you excel academically.</li>" +
                "</ul>" +
                "<p style='font-size:16px;'>If you have any questions or need assistance, feel free to reach out to us anytime.</p>" +
                "</body></html>";

        sendHtmlMail(toEmail, subject, body);
    }

    public void sendCgpaFile(String toEmail, double cgpa, byte[] pdfContent) {
        String subject = "Your CGPA Calculation";
        String body = "<html><body>" +
                "<p style='font-size:16px;'>Please find attached your CGPA calculation.</p>" +
                "<p style='font-size:16px;'>Your calculated CGPA is: <strong>" + cgpa + "</strong></p>" +
                "</body></html>";

        ByteArrayResource pdfResource = new ByteArrayResource(pdfContent);

        sendMailWithAttachment(toEmail, subject, body, pdfResource);
    }

    private void sendHtmlMail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendMailWithAttachment(String toEmail, String subject, String body, ByteArrayResource attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.addAttachment("cgpa_report.pdf", attachment);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
