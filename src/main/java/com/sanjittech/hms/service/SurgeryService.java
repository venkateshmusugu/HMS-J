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

    // Used only if creating surgery log directly without appointment
    public void bookSurgery(Long patientId, SurgeryLogDto dto) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Surgery surgery = Surgery.builder()
                .patient(patient)
                .surgeryDate(dto.getSurgeryDate())
                .medication(dto.getMedication())
                .reason(dto.getReason())
                .remarks(dto.getRemarks())
                .diagnosis(dto.getDiagnosis())
                .followUpDate(dto.getFollowUpDate())
                .medicines(dto.getMedicines() != null ? (List) dto.getMedicines() : List.of())
                .build();

        surgeryRepo.save(surgery);
    }

    // ✅ Recommended: Finalize a surgery by linking it to an appointment
    public Surgery logCompletedSurgery(Long appointmentId, SurgeryLogDto dto) {
        SurgeryAppointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Surgery appointment not found"));

        Surgery surgery = Surgery.builder()
                .patient(appointment.getPatient())
                .surgeryDate(dto.getSurgeryDate())
                .medication(dto.getMedication())
                .reason(dto.getReason())
                .remarks(dto.getRemarks())
                .diagnosis(dto.getDiagnosis())
                .followUpDate(dto.getFollowUpDate())
                .medicines(dto.getMedicines() != null ? (List) dto.getMedicines() : List.of())
                .build();

        surgeryRepo.save(surgery);

        appointment.setSurgeryLog(surgery);
        appointmentRepo.save(appointment);

        return surgery;
    }

    public Surgery updateSurgery(Long id, SurgeryLogDto dto) {
        Surgery surgery = surgeryRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Surgery not found"));

        surgery.setSurgeryDate(dto.getSurgeryDate());
        surgery.setMedication(dto.getMedication());
        surgery.setReason(dto.getReason());
        surgery.setRemarks(dto.getRemarks());
        surgery.setDiagnosis(dto.getDiagnosis());
        surgery.setFollowUpDate(dto.getFollowUpDate());

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
}
