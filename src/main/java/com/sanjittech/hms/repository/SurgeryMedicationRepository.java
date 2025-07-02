//package com.sanjittech.hms.repository;
//
//import com.sanjittech.hms.model.SurgeryMedication;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//
//public interface SurgeryMedicationRepository extends JpaRepository<SurgeryMedication, Long> {
//    List<SurgeryMedication> findBySurgeryAppointmentId(Long surgeryAppointmentId);
//}