package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.Surgery;
import com.sanjittech.hms.model.SurgeryAppointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
    List<Surgery> findBySurgeryDate(LocalDate date);
    List<Surgery> findByPatient_PatientId(Long patientId);
    Optional<Surgery> findByIdAndHospital(Long id, Hospital hospital); // ✅ this matches field "id"
}
