package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.SurgeryLogDto;
import com.sanjittech.hms.dto.SurgeryMedicationDTO;
import com.sanjittech.hms.model.*;
import com.sanjittech.hms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

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

    @Autowired
    private MedicalBillRepository medicalBillRepository;

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

    public List<Map<String, Object>> getMedicationLogsBySurgery(Long surgeryId) {
        List<MedicalBillEntry> entries = medRepo.findBySurgery_Id(surgeryId);

        // Group entries by date
        Map<LocalDate, List<MedicalBillEntry>> groupedByDate = new TreeMap<>(Comparator.reverseOrder());
        for (MedicalBillEntry entry : entries) {
            LocalDate date = entry.getDate() != null ? entry.getDate() : LocalDate.now();
            groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(entry);
        }

        List<Map<String, Object>> logs = new ArrayList<>();
        for (Map.Entry<LocalDate, List<MedicalBillEntry>> entry : groupedByDate.entrySet()) {
            Map<String, Object> log = new HashMap<>();
            LocalDate logDate = entry.getKey();
            List<MedicalBillEntry> dateEntries = entry.getValue();

            SurgeryAppointment appt = dateEntries.get(0).getSurgery();
            log.put("date", logDate.toString());
            log.put("diagnosis", appt != null && appt.getDiagnosis() != null ? appt.getDiagnosis() : "N/A");
            log.put("reasonForSurgery", appt != null && appt.getReason() != null ? appt.getReason() : "N/A");

            List<Map<String, Object>> meds = new ArrayList<>();
            for (MedicalBillEntry e : dateEntries) {
                Map<String, Object> medMap = new HashMap<>();
                medMap.put("medicineName", e.getMedicine() != null ? e.getMedicine().getName() : "N/A");
                medMap.put("dosage", e.getMedicine() != null ? e.getMedicine().getDosage() : "N/A");
                medMap.put("durationInDays", e.getDurationInDays());
                medMap.put("frequency", e.getFrequency());
                meds.add(medMap);
            }

            log.put("medicines", meds);
            logs.add(log);
        }

        return logs;
    }


    public void saveMedicationsForSurgery(Long surgeryAppointmentId, SurgeryMedicationDTO dto) {
        SurgeryAppointment appointment = appointmentRepo.findById(surgeryAppointmentId)
                .orElseThrow(() -> new RuntimeException("Surgery appointment not found"));

        // Update diagnosis, reason, followUp
        appointment.setDiagnosis(dto.getDiagnosis());
        appointment.setReason(dto.getReasonForSurgery());
        appointment.setFollowUpDate(LocalDate.parse(dto.getFollowUpDate()));
        appointmentRepo.save(appointment);

        Patient patient = appointment.getPatient();

        MedicalBill bill = medicalBillRepository
                .findByPatientAndStatus(patient, "OPEN")
                .orElseGet(() -> {
                    MedicalBill newBill = new MedicalBill();
                    newBill.setPatient(patient);
                    newBill.setBillDate(LocalDate.now());
                    newBill.setStatus("OPEN");
                    return medicalBillRepository.save(newBill);
                });

        List<MedicalBillEntry> entries = new ArrayList<>();

        for (MedicalBillEntry entry : dto.getMedicines()) {
            Medicine medicine;

            String medName = entry.getMedicineName();
            String dosage = entry.getDosage();

            if (medName == null || dosage == null) {
                throw new IllegalArgumentException("Medicine name or dosage is missing");
            }

            medicine = medicineRepo.findByNameIgnoreCaseAndDosageIgnoreCase(medName, dosage)
                    .orElseGet(() -> {
                        Medicine newMed = Medicine.builder()
                                .name(medName)
                                .dosage(dosage)
                                .amount(0.0)  // or any default
                                .build();
                        return medicineRepo.save(newMed);
                    });

            // Set to entry
            entry.setMedicine(medicine);
            entry.setSurgery(appointment);
            entry.setPatient(patient);
            entry.setPurpose("SURGERY");
            entry.setMedicalBill(bill);

            if (entry.getQuantity() == null || entry.getQuantity() <= 0)
                entry.setQuantity(1);

            if (entry.getIssuedQuantity() == null || entry.getIssuedQuantity() <= 0)
                entry.setIssuedQuantity(1);

            entry.setSubtotal(medicine.getAmount() * entry.getIssuedQuantity());
            entry.setDate(LocalDate.now());

            entries.add(entry);
        }



        medRepo.saveAll(entries);
    }



    public Optional<SurgeryAppointment> getSurgeryAppointmentById(Long id) {
        return appointmentRepo.findById(id);
    }

}
