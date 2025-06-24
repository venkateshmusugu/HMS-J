package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.SurgeryMedicationDTO;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.model.SurgeryMedication;
import com.sanjittech.hms.service.SurgeryMedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/surgery-medications")
public class SurgeryMedicationController {

    @Autowired
    private SurgeryMedicationService service;

    @PostMapping
    public ResponseEntity<?> saveMedication(@RequestBody SurgeryMedicationDTO dto) {
        SurgeryMedication med = new SurgeryMedication();
        med.setSurgeryAppointment(new SurgeryAppointment(dto.getSurgeryAppointmentId()));
        med.setName(dto.getName());
        med.setDosage(dto.getDosage());
        med.setFrequency(dto.getFrequency());
        med.setDurationInDays(dto.getDurationInDays());
        med.setDate(dto.getDate());

        service.save(med);
        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/by-surgery/{id}")
    public List<SurgeryMedication> getBySurgery(@PathVariable Long id) {
        return service.getBySurgeryAppointmentId(id);
    }
}