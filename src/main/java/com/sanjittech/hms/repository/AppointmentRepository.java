package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Get appointments after a specific date (not currently used, but may be useful for future)
    List<Appointment> findByVisitDateAfter(LocalDate date);

    // Get appointments before a specific date (not currently used)
    List<Appointment> findByVisitDateBefore(LocalDate date);

    // Get appointments for a specific date and filter by patient name or phone
    @Query("SELECT a FROM Appointment a WHERE a.visitDate = :date AND " +
            "(LOWER(a.patient.patientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR a.patient.phoneNumber LIKE CONCAT('%', :searchTerm, '%'))")
    List<Appointment> findByVisitDateAndPatientNameOrMobile(
            @Param("date") LocalDate date,
            @Param("searchTerm") String searchTerm
    );

    // Search only by patient name or phone (no date)
    @Query("SELECT a FROM Appointment a WHERE " +
            "LOWER(a.patient.patientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR a.patient.phoneNumber LIKE CONCAT('%', :searchTerm, '%')")
    List<Appointment> findByPatientNameOrMobile(@Param("searchTerm") String searchTerm);

    // Get appointments for a specific date
    List<Appointment> findByVisitDate(LocalDate date);

    // Get all appointments sorted by visit date (used when no searchTerm or date is provided)
    List<Appointment> findAllByOrderByVisitDateAsc();
}
