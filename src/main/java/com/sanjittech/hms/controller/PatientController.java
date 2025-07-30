package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.PatientDTO;
import com.sanjittech.hms.dto.PatientSuggestionDTO;
import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.service.PatientService;
import com.sanjittech.hms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserService userService;

    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody PatientDTO dto, HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid hospital/user context.");
        }

        Patient patient = Patient.builder()
                .patientName(dto.getPatientName())
                .gender(dto.getGender())
                .phoneNumber(dto.getPhoneNumber())
                .age(dto.getAge())
                .dob(dto.getDob())
                .maritalStatus(dto.getMaritalStatus())
                .address(dto.getCaseDescription())
                .registrationDate(LocalDate.now())
                .hospital(user.getHospital()) // ✅ Set hospital
                .build();

        Patient saved = patientService.savePatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients(HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Patient> patients = patientService.getAllPatientsByHospital(user.getHospital());
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patients/registered-today")
    public ResponseEntity<List<Patient>> getTodayPatients(HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Patient> todayPatients = patientService.getTodayRegisteredPatients(user.getHospital());
        return ResponseEntity.ok(todayPatients);
    }

    @GetMapping("/patients/search")
    public ResponseEntity<List<PatientSuggestionDTO>> searchPatients(@RequestParam String query, HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Patient> matched = patientService.searchPatientsByNameOrMobile(query, user.getHospital());

        List<PatientSuggestionDTO> suggestions = matched.stream()
                .map(p -> new PatientSuggestionDTO(p.getPatientId(), p.getPatientName(), p.getPhoneNumber()))
                .toList();

        return ResponseEntity.ok(suggestions);
    }
}
