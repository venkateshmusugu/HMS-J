package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.SurgeryLogDto;
import com.sanjittech.hms.model.*;
import com.sanjittech.hms.repository.*;
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
    private MedicineRepository medicineRepo;

    @Autowired
    private SurgeryAppointmentRepository appointmentRepo;

    @Autowired
    private MedicalBillEntryRepository medRepo;

    public void bookSurgery(Long patientId, SurgeryLogDto dto) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Surgery surgery = Surgery.builder()
                .patient(patient)
                .surgeryDate(dto.getDate() != null ? LocalDate.parse(dto.getDate()) : null)
                .reasonForSurgery(dto.getReasonForSurgery())
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
                .reasonForSurgery(dto.getReasonForSurgery())
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
        surgery.setReasonForSurgery(dto.getReasonForSurgery());
        surgery.setDiagnosis(dto.getDiagnosis());

        return surgeryRepo.save(surgery);
    }

    public void deleteSurgery(Long id) {
        medRepo.deleteBySurgery_Id(id);  // Delete associated medication entries
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

    public List<MedicalBillEntry> getMedicationsForSurgery(Long surgeryAppointmentId) {
        return medRepo.findBySurgery_Id(surgeryAppointmentId);
    }

    public void saveMedicationsForSurgery(Long surgeryAppointmentId, List<MedicalBillEntry> entries) {
        SurgeryAppointment appointment = appointmentRepo.findById(surgeryAppointmentId)
                .orElseThrow(() -> new RuntimeException("Surgery appointment not found"));

        for (MedicalBillEntry entry : entries) {
            if (entry.getMedicine() == null || entry.getMedicine().getId() == null || entry.getMedicine().getId() == 0) {
                throw new IllegalArgumentException("Invalid medicine: ID is missing or zero");
            }

            Medicine medicine = medicineRepo.findById(entry.getMedicine().getId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found with ID: " + entry.getMedicine().getId()));

            entry.setMedicine(medicine);
            entry.setSurgery(appointment);
            entry.setPatient(appointment.getPatient()); // ✅ set patient
            entry.setPurpose("SURGERY");

            // ✅ Ensure quantity is set
            if (entry.getQuantity() == null || entry.getQuantity() <= 0)
                entry.setQuantity(1);

            if (entry.getIssuedQuantity() == null || entry.getIssuedQuantity() <= 0)
                entry.setIssuedQuantity(1);

            entry.setSubtotal(medicine.getAmount() * entry.getIssuedQuantity());
        }

        medRepo.saveAll(entries);
    }

}
