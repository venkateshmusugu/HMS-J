package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.AppointmentDTO;
import com.sanjittech.hms.model.Appointment;
import com.sanjittech.hms.model.AppointmentStatus;
import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.repository.AppointmentRepository;
import com.sanjittech.hms.repository.DoctorRepository;
import com.sanjittech.hms.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public Appointment saveAppointmentFromDTO(AppointmentDTO dto) {
        if (dto.getDoctorId() == null) throw new IllegalArgumentException("Doctor ID must not be null");
        if (dto.getPatientId() == null) throw new IllegalArgumentException("Patient ID must not be null");

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // Check slot logic
        boolean available = isSlotAvailable(dto.getVisitDate(),
                dto.getStartTime().toString(),
                dto.getEndTime().toString(),
                dto.getDoctorId());

        if (!available) throw new RuntimeException("❌ Time slot already booked.");

        Appointment appt = Appointment.builder()
                .visitDate(dto.getVisitDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .reasonForVisit(dto.getReasonForVisit())
                .doctor(doctor)
                .patient(patient)
                .build();

        return appointmentRepository.save(appt);
    }




    public Appointment updateAppointment(Long id, Appointment appointment) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));

        Long doctorId = appointment.getDoctor().getDoctorId();
        LocalDate date = appointment.getVisitDate();
        LocalTime start = appointment.getStartTime();
        LocalTime end = appointment.getEndTime();

        List<Appointment> overlapping = appointmentRepository.findOverlappingAppointmentsWithExclusion(
                doctorId, date, start, end, id
        );

        if (!overlapping.isEmpty()) {
            throw new RuntimeException("❌ Updated time slot overlaps with another appointment.");
        }

        // Update fields
        existingAppointment.setVisitDate(date);
        existingAppointment.setStartTime(start);
        existingAppointment.setEndTime(end);
        existingAppointment.setDepartmentId(appointment.getDepartmentId());
        existingAppointment.setReasonForVisit(appointment.getReasonForVisit());

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        existingAppointment.setDoctor(doctor);

        Patient patient = patientRepository.findById(appointment.getPatient().getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        existingAppointment.setPatient(patient);

        return appointmentRepository.save(existingAppointment);
    }

    // ✅ Get one appointment by ID
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    // ✅ Get all appointments sorted by date
    public List<Appointment> getAllSortedByDate() {
        return appointmentRepository.findAllByOrderByVisitDateAsc();
    }

    // ✅ Find by visit date
    public List<Appointment> findByDate(LocalDate date) {
        return appointmentRepository.findByDateWithDoctorAndPatient(date);
    }


    // ✅ Search by name or phone
    public List<Appointment> findByPatientNameOrMobile(String searchTerm) {
        return appointmentRepository.findByPatientNameOrMobile(searchTerm);
    }

    // ✅ Filter by date and name/phone
    public List<Appointment> findByDateAndPatientNameOrMobile(LocalDate date, String searchTerm) {
        return appointmentRepository.findByVisitDateAndPatientNameOrMobile(date, searchTerm);
    }

    public boolean isSlotAvailable(LocalDate date, String startTime, String endTime, Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByVisitDateAndDoctorId(date, doctorId);

        LocalTime newStart = LocalTime.parse(startTime);
        LocalTime newEnd = LocalTime.parse(endTime);

        for (Appointment a : appointments) {
            LocalTime existingStart = a.getStartTime();
            LocalTime existingEnd = a.getEndTime();


            boolean overlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            if (overlap) {
                return false;
            }
        }

        return true;
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment bookAppointmentWithPatientId(Long patientId, AppointmentDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with ID: " + patientId));

        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + dto.getDoctorId()));

        Appointment appointment = Appointment.builder()
                .visitDate(dto.getVisitDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .reasonForVisit(dto.getReasonForVisit())
                .doctor(doctor)
                .patient(patient)
                .status(AppointmentStatus.ACTIVE)
                .build();

        return appointmentRepository.save(appointment);
    }


}
