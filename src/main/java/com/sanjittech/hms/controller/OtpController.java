package com.sanjittech.hms.controller;

import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.dto.OtpVerifyRequest;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.service.OtpService;
import com.sanjittech.hms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users/otp")
@CrossOrigin(origins = "http://localhost:3002")
public class OtpController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        otpService.sendOtp(email);
        return ResponseEntity.ok("OTP sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request) {
        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP");
        }

        otpService.invalidateOtp(request.getEmail());

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setRole(UserRole.valueOf(request.getRole().toUpperCase()));

        try {
            userService.register(user); // <-- this might throw
            return ResponseEntity.ok("OTP verified & user registered");
        } catch (RuntimeException ex) {
            // Instead of letting it bubble to 403/500, catch and return 400 with message
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        boolean isValid = otpService.verifyOtp(email, otp);
        if (!isValid) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        otpService.invalidateOtp(email);
        userService.updatePasswordByEmail(email, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }




}

