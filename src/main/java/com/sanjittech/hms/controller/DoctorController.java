package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.DoctorDTO;
import com.sanjittech.hms.dto.DoctorProfileDTO;
import com.sanjittech.hms.model.Department;
import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.DepartmentRepository;
import com.sanjittech.hms.repository.DoctorRepository;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public List<DoctorDTO> getDoctorsWithDepartment() {
        return doctorService.getDoctorsWithDepartment();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getDoctorCount() {
        long count = doctorService.getDoctorCount();
        return ResponseEntity.ok(count);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDoctor(@RequestBody Doctor doctor) {
        if (doctor.getDepartment() == null || doctor.getDepartment().getDepartmentId() == null) {
            return ResponseEntity.badRequest().body("Department ID is required");
        }

        Optional<Department> deptOpt = departmentRepository.findById(doctor.getDepartment().getDepartmentId());
        if (deptOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid department ID");
        }

        doctor.setDepartment(deptOpt.get());
        doctorRepository.save(doctor);
        return ResponseEntity.ok("Doctor added successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDoctorProfile(Authentication authentication) {
        System.out.println("🧠 Checking doctor profile for user: " + authentication.getName());
        return doctorRepository.findByUser_Username(authentication.getName())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor profile not set"));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> completeDoctorProfile(@RequestBody DoctorProfileDTO dto, Principal principal) {
        if (dto.getDepartmentName() == null || dto.getDoctorName() == null) {
            return ResponseEntity.badRequest().body("Doctor name and department name are required");
        }

        // Create or fetch department
        Department department = departmentRepository
                .findByDepartmentNameIgnoreCase(dto.getDepartmentName())
                .orElseGet(() -> departmentRepository.save(new Department(dto.getDepartmentName())));

        // Get the logged-in user
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if doctor profile already exists
        Optional<Doctor> existing = doctorRepository.findByUser_Username(principal.getName());

        Doctor doctor;
        if (existing.isPresent()) {
            // Update existing profile
            doctor = existing.get();
            doctor.setDoctorName(dto.getDoctorName());
            doctor.setDepartment(department);
        } else {
            // Create new profile
            doctor = Doctor.builder()
                    .doctorName(dto.getDoctorName())
                    .department(department)
                    .user(user)
                    .build();
        }

        return ResponseEntity.ok(doctorRepository.save(doctor));
    }



}
