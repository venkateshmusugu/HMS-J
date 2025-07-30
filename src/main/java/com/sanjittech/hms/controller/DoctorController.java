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
import com.sanjittech.hms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired private DoctorService doctorService;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private UserService userService;

    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getDoctorsWithDepartment(HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(doctorService.getDoctorsWithDepartment(user.getHospital()));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getDoctorCount(HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(doctorService.getDoctorCount(user.getHospital()));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addDoctor(@RequestBody Doctor doctor, HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        if (doctor.getDepartment() == null || doctor.getDepartment().getDepartmentId() == null) {
            return ResponseEntity.badRequest().body("Department ID is required");
        }

        Optional<Department> deptOpt = departmentRepository
                .findByDepartmentIdAndHospital(doctor.getDepartment().getDepartmentId(), user.getHospital());


        if (deptOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid department for your hospital");
        }

        doctor.setDepartment(deptOpt.get());
        doctor.setHospital(user.getHospital());
        doctorRepository.save(doctor);

        return ResponseEntity.ok("Doctor added successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyDoctorProfile(Authentication authentication) {
        return doctorRepository.findByUser_Username(authentication.getName())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor profile not set"));
    }

    @PostMapping("/profile")
    public ResponseEntity<?> completeDoctorProfile(@RequestBody DoctorProfileDTO dto, Principal principal) {
        if (dto.getDepartmentName() == null || dto.getDoctorName() == null) {
            return ResponseEntity.badRequest().body("Doctor name and department name are required");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Department department = departmentRepository
                .findByDepartmentNameIgnoreCaseAndHospital(dto.getDepartmentName(), user.getHospital())
                .orElseGet(() -> {
                    Department dep = new Department(dto.getDepartmentName());
                    dep.setHospital(user.getHospital());
                    return departmentRepository.save(dep);
                });

        Optional<Doctor> existing = doctorRepository.findByUser_Username(principal.getName());
        Doctor doctor = existing.orElseGet(Doctor::new);

        doctor.setDoctorName(dto.getDoctorName());
        doctor.setDepartment(department);
        doctor.setUser(user);
        doctor.setHospital(user.getHospital());

        return ResponseEntity.ok(doctorRepository.save(doctor));
    }

    @GetMapping("/by-hospital/{id}")
    public ResponseEntity<List<DoctorDTO>> getDoctorsByHospitalId(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorsWithDepartmentByHospitalId(id));
    }
}
