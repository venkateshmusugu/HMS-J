package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.PatientDTO;
import com.sanjittech.hms.dto.PatientSuggestionDTO;
import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody PatientDTO dto) {
        Patient patient = Patient.builder()
                .patientName(dto.getPatientName())
                .gender(dto.getGender())
                .phoneNumber(dto.getPhoneNumber())
                .age(dto.getAge())
                .dob(dto.getDob())
                .maritalStatus(dto.getMaritalStatus())
                .address(dto.getCaseDescription())
                .registrationDate(LocalDate.now())
                .build();

        Patient saved = patientService.savePatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }



    @GetMapping("/patients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patients/registered-today")
    public ResponseEntity<List<Patient>> getTodayPatients() {
        List<Patient> todayPatients = patientService.getTodayRegisteredPatients();
        return ResponseEntity.ok(todayPatients);
    }

    @GetMapping("/patients/search")
    public ResponseEntity<List<PatientSuggestionDTO>> searchPatients(@RequestParam String query) {
        List<Patient> matched = patientService.searchPatientsByNameOrMobile(query);

        List<PatientSuggestionDTO> suggestions = matched.stream()
                .map(p -> new PatientSuggestionDTO(p.getPatientId(), p.getPatientName(), p.getPhoneNumber()))
                .toList();

        return ResponseEntity.ok(suggestions);
    }

}
