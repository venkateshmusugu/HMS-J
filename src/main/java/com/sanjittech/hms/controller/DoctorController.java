package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.DoctorDTO;
import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public List<DoctorDTO> getDoctorsWithDepartment() {
        return doctorService.getDoctorsWithDepartment();
    }
}
