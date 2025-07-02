package com.sanjittech.hms.controller;

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
    public ResponseEntity<?> saveForSurgery(@PathVariable Long surgeryId, @RequestBody List<MedicalBillEntry> entries) {
        // Remove entry.setDate(...) – field does not exist in MedicalBillEntry
        surgeryService.saveMedicationsForSurgery(surgeryId, entries);
        return ResponseEntity.ok("Saved");
    }


    @GetMapping("/by-surgery/{surgeryId}")
    public ResponseEntity<Map<String, Object>> getMedsBySurgery(@PathVariable Long surgeryId) {
        List<MedicalBillEntry> entries = surgeryService.getMedicationsForSurgery(surgeryId);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());

        // Extracting diagnosis/reason from SurgeryAppointment
        if (!entries.isEmpty() && entries.get(0).getSurgery() != null) {
            SurgeryAppointment appointment = entries.get(0).getSurgery();
            response.put("diagnosis", appointment.getDiagnosis() != null ? appointment.getDiagnosis() : "N/A");
            response.put("reasonForSurgery", appointment.getReason() != null ? appointment.getReason() : "N/A");
        } else {
            response.put("diagnosis", "N/A");
            response.put("reasonForSurgery", "N/A");
        }

        response.put("medicines", entries);
        return ResponseEntity.ok(response);
    }

}
