package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.SurgeryLogDto;
import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.model.Surgery;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.repository.PatientRepository;
import com.sanjittech.hms.repository.SurgeryAppointmentRepository;
import com.sanjittech.hms.repository.SurgeryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class SurgeryService {

    @Autowired
    private SurgeryRepository surgeryRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private SurgeryAppointmentRepository appointmentRepo;

    public void bookSurgery(Long patientId, SurgeryLogDto dto) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Surgery surgery = Surgery.builder()
                .patient(patient)
                .surgeryDate(dto.getDate() != null ? LocalDate.parse(dto.getDate()) : null)
                .reason(dto.getReasonForSurgery())
                .diagnosis(dto.getDiagnosis())
                .build();

        surgeryRepo.save(surgery);
    }

    public Surgery logCompletedSurgery(Long appointmentId, SurgeryLogDto dto) {
        SurgeryAppointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Surgery appointment not found"));

        Surgery surgery = Surgery.builder()
                .patient(appointment.getPatient())
                .surgeryDate(dto.getDate() != null ? LocalDate.parse(dto.getDate()) : null)
                .reason(dto.getReasonForSurgery())
                .diagnosis(dto.getDiagnosis())
                .build();

        surgeryRepo.save(surgery);

        appointment.setSurgeryLog(surgery);
        appointmentRepo.save(appointment);

        return surgery;
    }

    public Surgery updateSurgery(Long id, SurgeryLogDto dto) {
        Surgery surgery = surgeryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Surgery not found"));

        surgery.setSurgeryDate(dto.getDate() != null ? LocalDate.parse(dto.getDate()) : null);
        surgery.setReason(dto.getReasonForSurgery());
        surgery.setDiagnosis(dto.getDiagnosis());

        return surgeryRepo.save(surgery);
    }

    public void deleteSurgery(Long id) {
        surgeryRepo.deleteById(id);
    }

    public List<Surgery> getSurgeriesByDate(LocalDate date) {
        return surgeryRepo.findBySurgeryDate(date);
    }

    public Surgery getSurgeryById(Long surgeryLogId) {
        return surgeryRepo.findById(surgeryLogId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Surgery not found"));
    }

    public List<Surgery> getSurgeriesByPatient(Long patientId) {
        return surgeryRepo.findByPatient_PatientId(patientId);
    }

    public List<SurgeryAppointment> getSurgeryAppointmentsByPatient(Long patientId) {
        return appointmentRepo.findByPatientPatientId(patientId);
    }




}
