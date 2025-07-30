package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByNameIgnoreCaseAndDosageIgnoreCase(String name, String dosage);
    boolean existsByNameIgnoreCaseAndDosageIgnoreCase(String name, String dosage);  // ✅ Fixed here
    // Scope queries by hospital
    List<Medicine> findByHospitalIdAndNameContainingIgnoreCase(Long hospitalId, String query);
    Optional<Medicine> findByHospitalIdAndNameIgnoreCaseAndDosageIgnoreCase(Long hospitalId, String name, String dosage);
    Optional<Medicine> findByNameIgnoreCaseAndDosageIgnoreCaseAndHospital_Id(String name, String dosage, Long hospitalId);
    boolean existsByNameIgnoreCaseAndDosageIgnoreCaseAndHospital_Id(String name, String dosage, Long hospitalId);
    List<Medicine> findByNameContainingIgnoreCaseAndHospital_Id(String query, Long hospitalId);


    List<Medicine> findByNameContainingIgnoreCase(String query);
}
