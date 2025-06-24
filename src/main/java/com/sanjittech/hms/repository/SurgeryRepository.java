package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Surgery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SurgeryRepository extends JpaRepository<Surgery, Long> {
    List<Surgery> findBySurgeryDate(LocalDate date);
}

