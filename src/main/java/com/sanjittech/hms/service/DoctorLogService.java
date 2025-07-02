package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.DoctorLogDTO;
import com.sanjittech.hms.dto.MedicalBillEntryDTO;
import com.sanjittech.hms.dto.MedicineDTO;
import com.sanjittech.hms.model.Appointment;
import com.sanjittech.hms.model.DoctorLog;
import com.sanjittech.hms.model.MedicalBillEntry;
import com.sanjittech.hms.model.Medicine;
import com.sanjittech.hms.repository.AppointmentRepository;
import com.sanjittech.hms.repository.DoctorLogRepo;
import com.sanjittech.hms.repository.MedicalBillEntryRepository;
import com.sanjittech.hms.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorLogService {

    @Autowired
    private DoctorLogRepo repo;
    @Autowired
    private MedicineRepository medicineRepository;


    @Autowired
    private AppointmentRepository apptRepo;

    @Autowired
    private MedicalBillEntryRepository medicalBillEntryRepo;

    public List<DoctorLog> findByAppointment(Long apptId) {
        return repo.findByAppointment_VisitId(apptId);
    }

    public List<Map<String, Object>> findMedicationsDetailedByPatient(Long patientId) {
        return repo.findAll().stream()
                .filter(dl -> dl.getPatient() != null && dl.getPatient().getPatientId().equals(patientId))
                .map(dl -> {
                    Map<String, Object> map = new HashMap<>();

                    // Fallback date
                    LocalDate date = dl.getFollowUpDate();
                    if (date == null && dl.getAppointment() != null && dl.getAppointment().getVisitDate() != null) {
                        date = dl.getAppointment().getVisitDate();
                    }
                    map.put("date", date != null ? date : LocalDate.now());

                    // Diagnosis fallback
                    map.put("diagnosis", dl.getDiagnosis() != null ? dl.getDiagnosis() : "N/A");

                    // Reason fallback
                    String reason = dl.getReasonForVisit();
                    if (reason == null && dl.getAppointment() != null) {
                        reason = dl.getAppointment().getReasonForVisit();
                    }
                    map.put("reasonForVisit", reason != null ? reason : "N/A");

                    // Medications via updated embedded fields in MedicalBillEntry
                    List<MedicalBillEntry> entries = medicalBillEntryRepo.findByDoctorLog_Id(dl.getId());

                    List<MedicineDTO> dtos = entries.stream().map(entry -> {
                        MedicineDTO dto = new MedicineDTO();
                        dto.setName(entry.getMedicineName());
                        dto.setDosage(entry.getDosage());
                        dto.setFrequency(entry.getFrequency());
                        dto.setDurationInDays(entry.getDurationInDays() != null ? entry.getDurationInDays() : 0);
                        return dto;
                    }).collect(Collectors.toList());

                    map.put("medicines", dtos);
                    return map;
                })
                .sorted(Comparator.comparing(m -> (LocalDate) m.get("date")))
                .collect(Collectors.toList());
    }

    public DoctorLog createLog(Long apptId, DoctorLog log) {
        Appointment appt = apptRepo.findById(apptId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        log.setAppointment(appt);
        log.setPatient(appt.getPatient());

        if (log.getReasonForVisit() == null || log.getReasonForVisit().isBlank()) {
            log.setReasonForVisit(appt.getReasonForVisit());
        }

        if (log.getFollowUpDate() == null) {
            log.setFollowUpDate(appt.getVisitDate());
        }

        if (log.getMedicineEntries() != null) {
            for (MedicalBillEntry entry : log.getMedicineEntries()) {
                entry.setDoctorLog(log);
                entry.setPurpose("DOCTOR");
            }
        }

        return repo.save(log);
    }

    public DoctorLog createLogFromDTO(Long apptId, DoctorLogDTO dto) {
        Appointment appt = apptRepo.findById(apptId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        DoctorLog log = new DoctorLog();
        log.setAppointment(appt);
        log.setPatient(appt.getPatient());
        log.setDiagnosis(dto.getDiagnosis());
        log.setReasonForVisit(dto.getReasonForVisit() != null ? dto.getReasonForVisit() : appt.getReasonForVisit());
        log.setFollowUpDate(dto.getFollowUpDate() != null ? dto.getFollowUpDate() : appt.getVisitDate());
        log.setFollowUpRequired(dto.isFollowUpRequired());
        log.setTestType(dto.getTestType());

        List<MedicalBillEntry> entries = new ArrayList<>();
        if (dto.getMedicines() != null) {
            for (MedicalBillEntryDTO m : dto.getMedicines()) {
                MedicalBillEntry entry = new MedicalBillEntry();
                entry.setDoctorLog(log);
                entry.setPurpose("DOCTOR");
                entry.setMedicineName(m.getMedicineName());
                entry.setDosage(m.getDosage());
                entry.setDurationInDays(m.getDurationInDays());
                entry.setFrequency(m.getFrequency());
                entry.setIssuedQuantity(1);
                entry.setQuantity(1);

                // ✅ Auto-fill amount if exists
                medicineRepository.findByNameIgnoreCaseAndDosageIgnoreCase(
                        m.getMedicineName(), m.getDosage()
                ).ifPresentOrElse(
                        med -> entry.setAmount(med.getAmount()),
                        () -> {
                            entry.setAmount(0.0);
                            // Optional: Add to master list if new
                            Medicine newMed = Medicine.builder()
                                    .name(m.getMedicineName())
                                    .dosage(m.getDosage())
                                    .amount(0.0)
                                    .build();
                            medicineRepository.save(newMed);
                        }
                );

                entries.add(entry);
            }
        }

        // ✅ This was missing in your code
        log.setMedicineEntries(entries);

        return repo.save(log);
    }
}
