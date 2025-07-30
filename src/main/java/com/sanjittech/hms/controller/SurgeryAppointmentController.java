package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.SurgeryAppointmentDTO;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.service.SurgeryAppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/surgery-appointments")
@CrossOrigin(origins = "http://localhost:3002")
public class SurgeryAppointmentController {

    @Autowired
    private SurgeryAppointmentService appointmentService;

    @PostMapping("/book/{patientId}")
    public ResponseEntity<SurgeryAppointment> book(@PathVariable Long patientId,
                                                   @RequestBody SurgeryAppointmentDTO dto,
                                                   HttpServletRequest request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(patientId, dto, request));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<SurgeryAppointmentDTO>> getByDate(@RequestParam("date") String dateStr,
                                                                 HttpServletRequest request) {
        LocalDate date = LocalDate.parse(dateStr);
        return ResponseEntity.ok(appointmentService.getAppointmentsWithDoctorPatientNote(date, request));
    }

    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<List<SurgeryAppointment>> getByPatient(@PathVariable Long patientId,
                                                                 HttpServletRequest request) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId, request));
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<SurgeryAppointment> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getById(id));
    }

    @PutMapping("/by-patient/{id}")
    public ResponseEntity<SurgeryAppointment> update(@PathVariable Long id,
                                                     @RequestBody SurgeryAppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok("Deleted successfully");
    }
}
