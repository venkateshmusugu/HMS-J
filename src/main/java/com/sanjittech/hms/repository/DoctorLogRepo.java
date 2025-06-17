package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.DoctorLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorLogRepo extends JpaRepository<DoctorLog, Long> {
    List<DoctorLog> findByAppointment_VisitId(Long visitId);
}
