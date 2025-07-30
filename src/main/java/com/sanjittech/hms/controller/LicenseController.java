    //package com.sanjittech.hms.controller;
//
//import com.sanjittech.hms.dto.LicenseRequest;
//import com.sanjittech.hms.model.Hospital;
//import com.sanjittech.hms.model.License;
//import com.sanjittech.hms.repository.HospitalRepository;
//import com.sanjittech.hms.repository.LicenseRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/license")
//@CrossOrigin(origins = "http://localhost:3002")
//public class LicenseController {
//
//    @Autowired
//    private HospitalRepository hospitalRepository;
//
//    @Autowired
//    private LicenseRepository licenseRepository;
//
//    @PostMapping("/create")
//    public ResponseEntity<String> createLicense(@RequestBody LicenseRequest request) {
//        try {
//            Optional<Hospital> hospitalOpt = hospitalRepository.findById(request.getHospitalId());
//
//            if (hospitalOpt.isEmpty()) {
//                return ResponseEntity.badRequest().body("Invalid hospital ID.");
//            }
//
//            Hospital hospital = hospitalOpt.get();
//            License license = licenseRepository.findByHospital_Id(hospital.getId());
//
//            if (license == null) {
//                license = new License();
//                license.setHospital(hospital);
//            }
//
//            // Set start date as today
//            license.setStartDate(LocalDate.now());
//
//            // Determine and set end date based on plan
//            LocalDate endDate = switch (request.getPlan().toUpperCase()) {
//                case "THREE_MONTHS" -> license.getStartDate().plusMonths(3);
//                case "SIX_MONTHS" -> license.getStartDate().plusMonths(6);
//                case "ONE_YEAR" -> license.getStartDate().plusYears(1);
//                default -> throw new IllegalArgumentException("Invalid plan: " + request.getPlan());
//            };
//
//            license.setEndDate(endDate);
//            license.setPlan(request.getPlan().toUpperCase());
//            license.setActive(true);
//
//            licenseRepository.save(license);
//
//            return ResponseEntity.ok("License activated successfully until " + endDate.toString());
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Error activating license: " + e.getMessage());
//        }
//    }
//}
