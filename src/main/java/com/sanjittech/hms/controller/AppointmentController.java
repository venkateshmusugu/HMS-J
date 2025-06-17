package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.Appointment;
import com.sanjittech.hms.repository.AppointmentRepository;
import com.sanjittech.hms.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);
        return ResponseEntity.ok(savedAppointment);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Appointment>> getAppointments(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Appointment> appointments;

        if (searchTerm != null && !searchTerm.isEmpty() && date != null) {
            appointments = appointmentService.findByDateAndPatientNameOrMobile(date, searchTerm);
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            appointments = appointmentService.findByPatientNameOrMobile(searchTerm);
        } else if (date != null) {
            appointments = appointmentService.findByDate(date);
        } else {
            appointments = appointmentService.getAllSortedByDate();
        }
        System.out.println("🔍 Logged-in user: " + SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println("👨‍⚕️ Returning " + appointments.size() + " appointments");

        return ResponseEntity.ok(appointments);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointment) {
        Appointment updatedAppointment = appointmentService.updateAppointment(id, appointment);
        return ResponseEntity.ok(updatedAppointment);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable Long id) {
        Appointment appt = appointmentService.findById(id);
        return ResponseEntity.ok(appt);
    }


}
