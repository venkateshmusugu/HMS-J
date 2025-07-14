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
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentDTO dto) {
        Appointment saved = appointmentService.saveAppointmentFromDTO(dto);
        return ResponseEntity.ok(saved);
    }
    @PostMapping("/book/{patientId}")
    public ResponseEntity<?> bookAppointmentForPatient(
            @PathVariable Long patientId,
            @RequestBody AppointmentDTO dto
    ) {
        try {
            Appointment appointment = appointmentService.bookAppointmentWithPatientId(patientId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Failed to book appointment: " + e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentDTO>> getAppointments(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long doctorId  // ✅ Add this line
    ) {
        System.out.println("🩺 doctorId received in controller: " + doctorId);
        List<Appointment> appointments;

        if (doctorId != null && date != null && searchTerm != null && !searchTerm.isEmpty()) {
            appointments = appointmentService.findByDoctorDateAndSearch(doctorId, date, searchTerm);
        } else if (doctorId != null && date != null) {
            appointments = appointmentService.findByDoctorAndDate(doctorId, date);
        } else if (date != null && searchTerm != null && !searchTerm.isEmpty()) {
            appointments = appointmentService.findByDateAndPatientNameOrMobile(date, searchTerm);
        } else if (doctorId != null) {
            appointments = appointmentService.findByDoctor(doctorId);
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            appointments = appointmentService.findByPatientNameOrMobile(searchTerm);
        } else if (date != null) {
            appointments = appointmentService.findByDate(date);
        } else {
            appointments = appointmentService.getAllSortedByDate();
        }

        List<AppointmentDTO> dtoList = appointments.stream().map(appt -> {
            AppointmentDTO dto = new AppointmentDTO();
            dto.setVisitId(appt.getVisitId());
            dto.setVisitDate(appt.getVisitDate());
            dto.setStartTime(appt.getStartTime());
            dto.setEndTime(appt.getEndTime());
            dto.setReasonForVisit(appt.getReasonForVisit());

            dto.setPatientId(appt.getPatient() != null ? appt.getPatient().getPatientId() : null);
            dto.setPatientName(appt.getPatient() != null ? appt.getPatient().getPatientName() : null);

            dto.setDoctorId(appt.getDoctor() != null ? appt.getDoctor().getDoctorId() : null);
            dto.setDoctorName(appt.getDoctor() != null ? appt.getDoctor().getDoctorName() : null);

            dto.setDepartmentId(
                    (appt.getDoctor() != null && appt.getDoctor().getDepartment() != null)
                            ? String.valueOf(appt.getDoctor().getDepartment().getDepartmentId())
                            : null
            );
            return dto;
        }).toList();

        return ResponseEntity.ok(dtoList);
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

    @GetMapping("/check-slot")
    public ResponseEntity<Boolean> checkSlot(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Long doctorId
    ) {
        boolean available = appointmentService.isSlotAvailable(date, startTime, endTime, doctorId);
        return ResponseEntity.ok(available);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {
        Appointment appt = appointmentService.getAppointmentById(id);
        if (appt != null) {
            appt.setStatus(AppointmentStatus.CANCELLED);  // Ensure this enum value exists
            appointmentRepository.save(appt);             // ✅ Save directly without validation
            return ResponseEntity.ok("Appointment cancelled successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found.");
        }
    }

//    @GetMapping("/booked-time-slots")
//    public ResponseEntity<List<String>> getBookedTimeSlots(
//            @RequestParam Long doctorId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
//    ) {
//        List<Appointment> appointments = appointmentService.findByDoctorAndDate(doctorId, date);
//
//        List<String> bookedStartTimes = appointments.stream()
//                .map(app -> app.getStartTime().toString())
//                .toList();
//
//        return ResponseEntity.ok(bookedStartTimes);
//    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
