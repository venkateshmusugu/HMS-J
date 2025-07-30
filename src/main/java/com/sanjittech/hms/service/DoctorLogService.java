package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.DoctorLogDTO;
import com.sanjittech.hms.dto.MedicalBillEntryDTO;
import com.sanjittech.hms.dto.MedicineDTO;
import com.sanjittech.hms.model.*;
import com.sanjittech.hms.repository.*;
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
    private MedicalBillService medicalBillService;
    @Autowired
    private MedicalBillRepository medicalBillRepository;
    @Autowired
    private AppointmentRepository apptRepo;

    @Autowired
    private MedicalBillEntryRepository medicalBillEntryRepo;

    public List<DoctorLog> findByAppointment(Long apptId, Long hospitalId) {
        Appointment appt = apptRepo.findById(apptId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appt.getHospital().getId().equals(hospitalId)) {
            throw new RuntimeException("Unauthorized: Appointment does not belong to this hospital");
        }

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
                    map.put("diagnosis", dl.getDiagnosis() != null ? dl.getDiagnosis() : "");

                    // Reason fallback
                    String reason = dl.getReasonForVisit();
                    if (reason == null && dl.getAppointment() != null) {
                        reason = dl.getAppointment().getReasonForVisit();
                    }
                    map.put("reasonForVisit", reason != null ? reason : "N/A");

                    // Medications via updated embedded fields in MedicalBillEntry
                    List<MedicalBillEntry> entries = medicalBillEntryRepo.findByDoctorLog_Id(dl.getId());

                    List<Map<String, Object>> medicines = entries.stream().map(entry -> {
                        Map<String, Object> medMap = new HashMap<>();
                        medMap.put("medicineName", entry.getMedicine().getName());
                        medMap.put("dosage", entry.getMedicine().getDosage());
                        medMap.put("durationInDays", entry.getDurationInDays());
                        medMap.put("frequency", entry.getFrequency());
                        return medMap;
                    }).collect(Collectors.toList());

                    map.put("medicines", medicines);
                    System.out.println("🧾 Prescription for: " + date);
                    System.out.println(medicines);
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

        DoctorLog savedLog = repo.save(log);

        List<MedicalBillEntry> entries = new ArrayList<>();

        if (dto.getMedicines() != null) {

            // ✅ Ensure OPEN bill exists or create one
            MedicalBill bill = medicalBillRepository
                    .findByPatientAndStatus(savedLog.getPatient(), "OPEN")
                    .orElseGet(() -> {
                        MedicalBill newBill = new MedicalBill();
                        newBill.setPatient(savedLog.getPatient());
                        newBill.setBillDate(LocalDate.now());
                        newBill.setStatus("OPEN");
                        return medicalBillRepository.save(newBill);
                    });

            for (MedicalBillEntryDTO m : dto.getMedicines()) {
                MedicalBillEntry entry = new MedicalBillEntry();
                entry.setDoctorLog(savedLog);
                entry.setPurpose("DOCTOR");
                entry.setDurationInDays(m.getDurationInDays());
                entry.setFrequency(m.getFrequency());
                entry.setIssuedQuantity(1);
                entry.setQuantity(1);
                entry.setPatient(savedLog.getPatient());
                entry.setMedicalBill(bill);

                // 🧾 Find or create medicine
                Medicine med = medicineRepository
                        .findByNameIgnoreCaseAndDosageIgnoreCase(m.getMedicineName(), m.getDosage())
                        .orElseGet(() -> {
                            Medicine newMed = Medicine.builder()
                                    .name(m.getMedicineName())
                                    .dosage(m.getDosage())
                                    .amount(0.0)
                                    .build();
                            return medicineRepository.save(newMed);
                        });

                entry.setMedicine(med);
                entry.setSubtotal(med.getAmount() * entry.getIssuedQuantity());
                entries.add(entry);
            }
            // 💾 Save all medicine entries
            medicalBillEntryRepo.saveAll(entries);
        }
        return savedLog;
    }
}
