package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.MedicalBillEntry;
import com.sanjittech.hms.model.MedicalBill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalBillEntryRepository extends JpaRepository<MedicalBillEntry, Long> {

    // 🔁 Existing methods for billing
    List<MedicalBillEntry> findByMedicalBill(MedicalBill bill);
    void deleteByMedicalBill(MedicalBill bill);

    // ✅ Add support for Doctor prescriptions
    List<MedicalBillEntry> findByDoctorLog_Id(Long doctorLogId);

    // ✅ Add support for Surgery medications
    List<MedicalBillEntry> findBySurgery_Id(Long surgeryId);

    void deleteBySurgery_Id(Long surgeryId);
}
