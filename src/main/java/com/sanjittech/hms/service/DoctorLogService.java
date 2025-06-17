package com.sanjittech.hms.service;

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
                    map.put("date", dl.getAppointment().getVisitDate());
                    List<Medicine> medicines = medicineRepo.findByDoctorLog_Id(dl.getId());
                    map.put("medicines", medicines);
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
        log.setDoctor(appt.getDoctor());

        return repo.save(log);
    }
}
