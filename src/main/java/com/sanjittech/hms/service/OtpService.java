package com.sanjittech.hms.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final JavaMailSender mailSender;

    // Thread-safe for multiple users
    private final ConcurrentHashMap<String, String> otpMap = new ConcurrentHashMap<>();

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Generate a 6-digit OTP
    public String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }

    // Send OTP to user's email
    public void sendOtp(String toEmail) {
        String otp = generateOtp();
        otpMap.put(toEmail, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Your HMS Registration OTP");
        message.setText("Dear user,\n\nYour OTP for registration is: " + otp + "\n\nThis code will expire in 5 minutes.\n\nRegards,\nHMS Team");

        mailSender.send(message);
        System.out.println("✅ OTP sent to: " + toEmail + " | OTP: " + otp);
    }

    // Verify OTP entered by user
    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpMap.get(email);
        return storedOtp != null && storedOtp.equals(otp);
    }

    // Remove OTP after successful verification
    public void invalidateOtp(String email) {
        otpMap.remove(email);
    }
}
