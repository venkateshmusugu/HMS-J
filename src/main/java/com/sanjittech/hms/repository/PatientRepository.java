package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByRegistrationDate(LocalDate date);
    List<Patient> findByPatientNameContainingIgnoreCaseOrPhoneNumberContaining(String name, String phone);

    Patient findByPhoneNumber(String phone);
}
