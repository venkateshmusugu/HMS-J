package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByRegistrationDate(LocalDate date);
    List<Patient> findByPatientNameContainingIgnoreCaseOrPhoneNumberContaining(String name, String phone);

    Patient findByPhoneNumber(String phone);
    List<Patient> findByHospital(Hospital hospital);


    List<Patient> findByHospitalAndRegistrationDate(Hospital hospital, LocalDate registrationDate);

    // For search:
    @Query("SELECT p FROM Patient p WHERE p.hospital = :hospital AND " +
            "(LOWER(p.patientName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "p.phoneNumber LIKE %:query%)")
    List<Patient> searchByHospitalAndQuery(@Param("hospital") Hospital hospital, @Param("query") String query);


    // ✅ Correct
    Optional<Patient> findByPatientIdAndHospital(Long patientId, Hospital hospital);

}
