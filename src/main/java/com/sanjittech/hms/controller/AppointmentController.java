package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.AppointmentDTO;
import com.sanjittech.hms.model.Appointment;
import com.sanjittech.hms.model.AppointmentStatus;
import com.sanjittech.hms.repository.AppointmentRepository;
import com.sanjittech.hms.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:3002")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO dto) {
        Appointment saved = appointmentService.saveAppointmentFromDTO(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/book/{patientId}")
    public ResponseEntity<?> bookAppointmentForPatient(
            @PathVariable Long patientId,
            @RequestBody AppointmentDTO dto) {
        try {
            Appointment appointment = appointmentService.bookAppointmentWithPatientId(patientId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Failed: " + e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentDTO>> getAppointments(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId ,
            @RequestParam Long hospitalId) {

        List<AppointmentDTO> dtoList = appointmentService.getFilteredAppointments(searchTerm, date, doctorId, hospitalId);
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
        Appointment updated = appointmentService.updateAppointment(id, appointment);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.findById(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long id) {
        return appointmentService.cancelAppointment(id);
    }

    @GetMapping("/check-slot")
    public ResponseEntity<Boolean> checkSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Long doctorId) {
        boolean available = appointmentService.isSlotAvailable(date, startTime, endTime, doctorId);
        return ResponseEntity.ok(available);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
