package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.SurgeryMedicationDTO;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.model.SurgeryMedication;
import com.sanjittech.hms.service.SurgeryMedicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/surgery-medications")
public class SurgeryMedicationController {

    @Autowired
    private SurgeryMedicationService service;

    @PostMapping("/by-surgery/{surgeryId}")
    public ResponseEntity<?> saveBySurgery(@PathVariable Long surgeryId, @RequestBody SurgeryMedicationDTO dto) {
        System.out.println("📥 Received POST for surgery ID: " + surgeryId);

        SurgeryMedication med = new SurgeryMedication();
        med.setSurgeryAppointment(new SurgeryAppointment(surgeryId));
        med.setName(dto.getMedicineName());
        med.setDosage(dto.getDosage());
        med.setFrequency(dto.getDuration());
        med.setComments(dto.getComments());
        med.setDiagnosis(dto.getDiagnosis());
        med.setDate(java.time.LocalDate.now()); // Set current date

        service.save(med);
        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/by-surgery/{id}")
    public List<Map<String, Object>> getBySurgery(@PathVariable Long id) {
        System.out.println("📥 Fetching medication logs for surgery ID: " + id);
        List<SurgeryMedication> meds = service.getBySurgeryAppointmentId(id);
        meds.forEach(m -> System.out.println("➡️ " + m.getDiagnosis()));

        // Group by date
        Map<String, List<SurgeryMedication>> grouped = meds.stream()
                .collect(Collectors.groupingBy(m -> m.getDate().toString()));

        List<Map<String, Object>> logs = new ArrayList<>();

        for (Map.Entry<String, List<SurgeryMedication>> entry : grouped.entrySet()) {
            String date = entry.getKey();
            List<SurgeryMedicationDTO> medDtos = entry.getValue().stream().map(med -> {
                return new SurgeryMedicationDTO(
                        med.getName(),
                        med.getDosage(),
                        med.getFrequency(),
                        med.getComments(),
                        med.getDiagnosis()
                );
            }).collect(Collectors.toList());

            // get reason and diagnosis from appointment
            SurgeryMedication first = entry.getValue().get(0);
            SurgeryAppointment appointment = first.getSurgeryAppointment();

            Map<String, Object> map = new HashMap<>();
            map.put("date", date);
            map.put("reasonForSurgery", appointment.getReason());
            map.put("diagnosis", appointment.getDiagnosis());
            map.put("medicines", medDtos);

            logs.add(map);
        }

        return logs;
    }
}
