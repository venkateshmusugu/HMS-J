package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.MedicalBill;
import com.sanjittech.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MedicalBillRepository extends JpaRepository<MedicalBill, Long> {
   // List<MedicalBill> findByMobile(String mobile);
   @Query("SELECT mb FROM MedicalBill mb WHERE mb.patient.phoneNumber = :mobile")
   List<MedicalBill> findByPatientMobile(@Param("mobile") String mobile);
    List<MedicalBill> findByBillDate(LocalDate billDate);


     Optional<MedicalBill> findByPatientAndStatus(Patient patient, String open);
}
