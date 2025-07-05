package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.SurgeryMedicationDTO;
import com.sanjittech.hms.model.MedicalBillEntry;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.service.SurgeryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/surgery-medications")
@CrossOrigin("http://localhost:3002")
public class SurgeryMedicationController {

    @Autowired
    private SurgeryService surgeryService;

    @PostMapping("/by-surgery/{surgeryId}")
    public ResponseEntity<?> saveForSurgery(@PathVariable Long surgeryId, @RequestBody SurgeryMedicationDTO dto) {
        surgeryService.saveMedicationsForSurgery(surgeryId, dto);
        return ResponseEntity.ok("Saved");
    }




    @GetMapping("/by-surgery/{surgeryId}")
    public ResponseEntity<Map<String, Object>> getMedsBySurgery(@PathVariable Long surgeryId) {
        List<Map<String, Object>> logs = surgeryService.getMedicationLogsBySurgery(surgeryId);

        Map<String, Object> response = new HashMap<>();
        response.put("logs", logs);

        // Get extra details from the appointment
        Optional<SurgeryAppointment> optional = surgeryService.getSurgeryAppointmentById(surgeryId); // ✳️ We'll add this in service
        if (optional.isPresent()) {
            SurgeryAppointment sa = optional.get();

            response.put("surgeryDate", sa.getSurgeryDate() != null ? sa.getSurgeryDate().toString() : "N/A");
            response.put("surgeryTime", sa.getSurgeryTime() != null ? sa.getSurgeryTime().toString() : "N/A");
            response.put("surgeryType", sa.getSurgeryType());
            response.put("remarks", sa.getRemarks());
            response.put("followUpDate", sa.getFollowUpDate() != null ? sa.getFollowUpDate().toString() : "N/A");

            response.put("diagnosis", sa.getDiagnosis() != null ? sa.getDiagnosis() : "N/A");
            response.put("reasonForSurgery", sa.getReason() != null ? sa.getReason() : "N/A");
        } else {
            response.put("diagnosis", "N/A");
            response.put("reasonForSurgery", "N/A");
            response.put("surgeryDate", LocalDate.now().toString());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> getAllSurgeryMedLogs(@PathVariable Long patientId) {
        List<SurgeryAppointment> appointments = surgeryService.getSurgeryAppointmentsByPatient(patientId);

        List<Map<String, Object>> result = new ArrayList<>();
        for (SurgeryAppointment sa : appointments) {
            Map<String, Object> item = new HashMap<>();
            item.put("surgeryId", sa.getId());
            item.put("surgeryDate", sa.getSurgeryDate());
            item.put("surgeryType", sa.getSurgeryType());
            item.put("remarks", sa.getRemarks());
            item.put("followUpDate", sa.getFollowUpDate());
            item.put("diagnosis", sa.getDiagnosis());
            item.put("reason", sa.getReason());

            // Get medicine logs grouped by date
            List<Map<String, Object>> medsGrouped = surgeryService.getMedicationLogsBySurgery(sa.getId());
            item.put("medicationLogs", medsGrouped);

            result.add(item);
        }

        return ResponseEntity.ok(result);
    }



}
