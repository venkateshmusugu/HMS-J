package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Surgery;
import com.sanjittech.hms.model.SurgeryAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
    List<Surgery> findBySurgeryDate(LocalDate date);

    List<Surgery> findByPatient_PatientId(Long patientId);
    List<SurgeryAppointment> findByPatientPatientId(Long patientId);


}

