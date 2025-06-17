package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends  JpaRepository<Medicine, Long> {
    List<Medicine> findByDoctorLog_Id(Long doctorLogId);
}
