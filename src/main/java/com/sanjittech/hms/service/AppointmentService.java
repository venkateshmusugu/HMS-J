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
        return appointmentRepository.findByVisitDate(date);
    }

    // ✅ Search by name or phone
    public List<Appointment> findByPatientNameOrMobile(String searchTerm) {
        return appointmentRepository.findByPatientNameOrMobile(searchTerm);
    }

    // ✅ Filter by date and name/phone
    public List<Appointment> findByDateAndPatientNameOrMobile(LocalDate date, String searchTerm) {
        return appointmentRepository.findByVisitDateAndPatientNameOrMobile(date, searchTerm);
    }
}
