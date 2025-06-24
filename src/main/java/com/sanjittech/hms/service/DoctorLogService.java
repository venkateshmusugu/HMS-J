package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.MedicineDTO;
import com.sanjittech.hms.model.Appointment;
import com.sanjittech.hms.model.DoctorLog;
import com.sanjittech.hms.model.Medicine;
import com.sanjittech.hms.repository.AppointmentRepository;
import com.sanjittech.hms.repository.DoctorLogRepo;
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
    private AppointmentRepository apptRepo;

    @Autowired
    private MedicineRepository medicineRepo;

    public List<DoctorLog> findByAppointment(Long apptId) {
        return repo.findByAppointment_VisitId(apptId);
    }

    public List<Map<String, Object>> findMedicationsDetailedByPatient(Long patientId) {
        return repo.findAll().stream()
                .filter(dl -> dl.getPatient().getPatientId().equals(patientId))
                .map(dl -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", dl.getFollowUpDate() != null ? dl.getFollowUpDate() : dl.getAppointment().getVisitDate());
                    map.put("diagnosis", dl.getDiagnosis() != null ? dl.getDiagnosis() : "N/A");
                    map.put("reasonForVisit", dl.getReasonForVisit() != null ? dl.getReasonForVisit() : (dl.getAppointment() != null ? dl.getAppointment().getReasonForVisit() : "N/A"));

                    // ✅ FETCH MEDICINES
                    List<Medicine> medicines = medicineRepo.findByDoctorLog_Id(dl.getId());

                    // 🔍 DEBUG EACH MEDICINE
                    for (Medicine m : medicines) {
                        System.out.println("🟢 Medicine Found:");
                        System.out.println("ID: " + m.getMedicineId());
                        System.out.println("Name: " + m.getName());
                        System.out.println("Dosage: " + m.getDosage());
                        System.out.println("Duration: " + m.getDurationInDays());
                        System.out.println("Frequency: " + m.getFrequency());
                    }

                    // ✅ CONVERT TO DTOs
                    List<MedicineDTO> medicineDTOs = medicines.stream().map(med -> {
                        MedicineDTO dto = new MedicineDTO();
                        dto.setName(med.getName());
                        dto.setDosage(med.getDosage());
                        dto.setFrequency(med.getFrequency());
                        dto.setDurationInDays(med.getDurationInDays());
                        return dto;
                    }).collect(Collectors.toList());

                    map.put("medicines", medicineDTOs);
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

        // Fallbacks if not provided by frontend
        if (log.getReasonForVisit() == null || log.getReasonForVisit().isBlank()) {
            log.setReasonForVisit(appt.getReasonForVisit());
        }

        if (log.getFollowUpDate() == null) {
            log.setFollowUpDate(appt.getVisitDate());
        }

        if (log.getMedicines() != null) {
            for (Medicine med : log.getMedicines()) {
                med.setDoctorLog(log);
            }
        }

        return repo.save(log);
    }
}
