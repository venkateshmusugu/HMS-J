package com.sanjittech.hms.service;

import com.sanjittech.hms.model.Appointment;
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

    // ✅ Create appointment
    public Appointment saveAppointment(Appointment appointment) {
        // Validate doctor
        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        appointment.setDoctor(doctor);

        // Validate patient
        Patient patient = patientRepository.findById(appointment.getPatient().getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        appointment.setPatient(patient);

        // Check for overlapping appointments
        List<Appointment> overlaps = appointmentRepository.findOverlappingAppointments(
                doctor.getDoctorId(),
                appointment.getVisitDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        if (!overlaps.isEmpty()) {
            throw new RuntimeException("❌ Time slot already booked for this doctor.");
        }
        System.out.println("🚨 Incoming doctorId: " +
                (appointment.getDoctor() != null ? appointment.getDoctor().getDoctorId() : "NULL"));


        return appointmentRepository.save(appointment);
    }

    // ✅ Update appointment with patient and doctor references
    public Appointment updateAppointment(Long id, Appointment appointment) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));

        existingAppointment.setVisitDate(appointment.getVisitDate());
        existingAppointment.setStartTime(appointment.getStartTime());
        existingAppointment.setEndTime(appointment.getEndTime());
        existingAppointment.setDepartmentId(appointment.getDepartmentId());
        existingAppointment.setReasonForVisit(appointment.getReasonForVisit());

        // Update doctor reference
        if (appointment.getDoctor() != null && appointment.getDoctor().getDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            existingAppointment.setDoctor(doctor);
        }

        // Update patient reference
        if (appointment.getPatient() != null && appointment.getPatient().getPatientId() != null) {
            Patient patient = patientRepository.findById(appointment.getPatient().getPatientId())
                    .orElseThrow(() -> new RuntimeException("Patient not found"));
            existingAppointment.setPatient(patient);
        }

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

}
