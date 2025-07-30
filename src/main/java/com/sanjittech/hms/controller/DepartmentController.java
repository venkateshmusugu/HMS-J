package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.Department;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.DepartmentRepository;
import com.sanjittech.hms.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> addDepartment(@RequestBody Department department, HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(401).body("Unauthorized or hospital not found");
        }

        department.setHospital(user.getHospital());
        Department saved = departmentRepository.save(department);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments(HttpServletRequest request) {
        User user = userService.getLoggedInUser(request);
        if (user == null || user.getHospital() == null) {
            return ResponseEntity.status(401).build();
        }

        List<Department> departments = departmentRepository.findByHospital(user.getHospital());
        return ResponseEntity.ok(departments);
    }
}
