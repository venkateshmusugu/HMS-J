package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.DoctorLogDTO;
import com.sanjittech.hms.model.DoctorLog;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.repository.SurgeryAppointmentRepository;
import com.sanjittech.hms.repository.SurgeryRepository;
import com.sanjittech.hms.service.DoctorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor-logs")
@CrossOrigin("http://localhost:3002")
public class    DoctorLogController {

    @Autowired
    private DoctorLogService service;

    @Autowired
    private SurgeryAppointmentRepository surgeryRepository;

    @GetMapping("/by-appointment/{apptId}")
    public ResponseEntity<List<DoctorLog>> getByAppointment(@PathVariable Long apptId) {
        return ResponseEntity.ok(service.findByAppointment(apptId));
    }

    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> medsDetailsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(service.findMedicationsDetailedByPatient(patientId));
    }

    @PostMapping("/by-appointment/{apptId}")
    public ResponseEntity<DoctorLog> createLog(
            @PathVariable Long apptId,
            @RequestBody DoctorLogDTO dto
    ) {
        return ResponseEntity.ok(service.createLogFromDTO(apptId, dto));
    }



}
