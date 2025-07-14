package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.Department;
import com.sanjittech.hms.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @PostMapping
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {
        // Optionally: Check for duplicates
        Department saved = departmentRepository.save(department);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
