package com.sanjittech.hms.service;

import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.repository.PatientRepository;
import com.sanjittech.hms.repository.SurgeryAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SurgeryAppointmentService {

    @Autowired
    private SurgeryAppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    public SurgeryAppointment bookAppointment(Long patientId, SurgeryAppointment dto) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        dto.setPatient(patient);
        return appointmentRepo.save(dto);
    }

    public List<SurgeryAppointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepo.findBySurgeryDate(date);
    }

    public List<SurgeryAppointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepo.findByPatientPatientId(patientId);
    }

    public void deleteAppointment(Long id) {
        appointmentRepo.deleteById(id);
    }

    public SurgeryAppointment getById(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public SurgeryAppointment updateAppointment(Long id, SurgeryAppointment updated) {
        SurgeryAppointment existing = getById(id);

        existing.setSurgeryDate(updated.getSurgeryDate());
        existing.setSurgeryTime(updated.getSurgeryTime());
        existing.setSurgeryType(updated.getSurgeryType());
        existing.setStatus(updated.getStatus());

        return appointmentRepo.save(existing);
    }
}
