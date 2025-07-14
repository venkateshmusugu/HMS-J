package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByNameIgnoreCaseAndDosageIgnoreCase(String name, String dosage);
    boolean existsByNameIgnoreCaseAndDosageIgnoreCase(String name, String dosage);  // ✅ Fixed here

    List<Medicine> findByNameContainingIgnoreCase(String query);
}
