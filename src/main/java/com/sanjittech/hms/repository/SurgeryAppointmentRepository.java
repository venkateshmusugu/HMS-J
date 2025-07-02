package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.SurgeryAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SurgeryAppointmentRepository extends JpaRepository<SurgeryAppointment, Long> {
    List<SurgeryAppointment> findBySurgeryDate(LocalDate date);
    List<SurgeryAppointment> findByPatientPatientId(Long patientId);
    Optional<SurgeryAppointment> findTopByPatient_PatientIdOrderBySurgeryDateDesc(Long patientId);
}
