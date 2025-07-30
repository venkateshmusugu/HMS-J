//package com.sanjittech.hms.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sanjittech.hms.config.UserRole;
//import com.sanjittech.hms.model.Hospital;
//import com.sanjittech.hms.model.License;
//import com.sanjittech.hms.model.User;
//import com.sanjittech.hms.repository.HospitalRepository;
//import com.sanjittech.hms.repository.LicenseRepository;
//import com.sanjittech.hms.repository.UserRepository;
//import com.stripe.exception.SignatureVerificationException;
//import com.stripe.model.Event;
//import com.stripe.model.checkout.Session;
//import com.stripe.net.Webhook;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/webhook")
//public class StripeWebhookController {
//
//    private final HospitalRepository hospitalRepository;
//    private final LicenseRepository licenseRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Value("${stripe.webhook.secret}")
//    private String endpointSecret;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    // ✅ SINGLE constructor with all dependencies
//    public StripeWebhookController(
//            HospitalRepository hospitalRepository,
//            LicenseRepository licenseRepository,
//            UserRepository userRepository,
//            PasswordEncoder passwordEncoder
//    ) {
//        this.hospitalRepository = hospitalRepository;
//        this.licenseRepository = licenseRepository;
//        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> handleStripeEvent(HttpServletRequest request) throws IOException {
//        StringBuilder payload = new StringBuilder();
//        try (BufferedReader reader = request.getReader()) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                payload.append(line);
//            }
//        }
//
//        String sigHeader = request.getHeader("Stripe-Signature");
//        Event event;
//
//        try {
//            event = Webhook.constructEvent(payload.toString(), sigHeader, endpointSecret);
//        } catch (SignatureVerificationException e) {
//            return ResponseEntity.status(400).body("Webhook signature verification failed.");
//        }
//
//        if ("checkout.session.completed".equals(event.getType())) {
//            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
//            if (session != null) {
//                String hospitalIdStr = session.getMetadata().get("hospitalId");
//                String plan = session.getMetadata().get("plan");
//                String adminEmail = session.getMetadata().get("adminEmail");
//                String adminPassword = session.getMetadata().get("adminPassword");
//
//                if (hospitalIdStr != null && plan != null) {
//                    Long hospitalId = Long.parseLong(hospitalIdStr);
//
//                    Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
//                    if (hospitalOpt.isPresent()) {
//                        Hospital hospital = hospitalOpt.get();
//
//                        // ✅ Set payment as done
//                        hospital.setPaymentDone(true);
//                        hospitalRepository.save(hospital);
//
//                        // ✅ Create license
//                        License license = licenseRepository.findByHospital_Id(hospitalId);
//                        if (license == null) {
//                            license = new License();
//                            license.setHospital(hospital);
//                        }
//
//                        license.setStartDate(LocalDate.now());
//                        LocalDate endDate = switch (plan.toUpperCase()) {
//                            case "THREE_MONTHS" -> license.getStartDate().plusMonths(3);
//                            case "SIX_MONTHS" -> license.getStartDate().plusMonths(6);
//                            case "ONE_YEAR" -> license.getStartDate().plusYears(1);
//                            default -> throw new IllegalArgumentException("Invalid plan");
//                        };
//
//                        license.setEndDate(endDate);
//                        license.setPlan(plan.toUpperCase());
//                        license.setActive(true);
//                        licenseRepository.save(license);
//
//                        // ✅ Create real admin using email/password
//                        boolean adminExists = userRepository.countByRoleAndHospital_Id(UserRole.ADMIN, hospitalId) > 0;
//                        if (!adminExists && adminEmail != null && adminPassword != null) {
//                            User admin = new User();
//                            admin.setUsername(adminEmail); // or extract name
//                            admin.setEmail(adminEmail);
//                            admin.setRole(UserRole.ADMIN);
//                            admin.setPassword(passwordEncoder.encode(adminPassword));
//                            admin.setHospital(hospital);
//                            userRepository.save(admin);
//                        }
//
//                        return ResponseEntity.ok("License and admin created via webhook");
//                    }
//                }
//            }
//        }
//
//
//        return ResponseEntity.ok("Event ignored");
//    }
//
//}
