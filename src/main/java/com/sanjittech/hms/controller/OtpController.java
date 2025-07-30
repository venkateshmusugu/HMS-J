package com.sanjittech.hms.controller;

import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.dto.OtpVerifyRequest;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.HospitalRepository;
import com.sanjittech.hms.service.AppointmentService;
import com.sanjittech.hms.service.OtpService;
import com.sanjittech.hms.service.UserService;
import com.sanjittech.hms.util.JwtUtil;
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
    private JwtUtil jwtUtil;
    @Autowired
    private HospitalRepository hospitalRepository;


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
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
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
            // Optional hospitalId if Authorization is provided
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Long hospitalId = jwtUtil.extractHospitalId(token);

                if (hospitalId != null) {
                    Hospital hospital = hospitalRepository.findById(hospitalId)
                            .orElseThrow(() -> new RuntimeException("Hospital not found"));
                    user.setHospital(hospital);
                }
            }

            userService.register(user);
            return ResponseEntity.ok("OTP verified & user registered");
        } catch (RuntimeException ex) {
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
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        try {
            User user = userService.login(username, password);  // ✅ This checks password

            // You can also generate token if needed
            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole().name(),
                    user.getHospital() != null ? user.getHospital().getId() : null
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "accessToken", token,
                    "role", user.getRole().name()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }




}

