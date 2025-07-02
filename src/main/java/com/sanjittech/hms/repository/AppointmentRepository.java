package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Appointment;
import com.sanjittech.hms.model.DoctorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Get appointments after a specific date (not currently used, but may be useful for future)
    List<Appointment> findByVisitDateAfter(LocalDate date);

    @Query("SELECT d FROM DoctorLog d JOIN FETCH d.doctor WHERE d.appointment.visitId = :visitId")
    List<DoctorLog> findByAppointmentIdWithDoctor(@Param("visitId") Long visitId);


    // Get appointments before a specific date (not currently used)
    List<Appointment> findByVisitDateBefore(LocalDate date);
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.doctor WHERE a.visitDate = :date")
    List<Appointment> findByVisitDateWithPatientAndDoctor(@Param("date") LocalDate date);



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

    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND a.visitDate = :date AND " +
            "((:startTime BETWEEN a.startTime AND a.endTime) OR " +
            "(:endTime BETWEEN a.startTime AND a.endTime) OR " +
            "(a.startTime BETWEEN :startTime AND :endTime))")
    List<Appointment> findOverlappingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime
    );
    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND a.visitDate = :date")
    List<Appointment> findByVisitDateWithDoctor(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date);

    @Query("SELECT a FROM Appointment a WHERE a.visitDate = :date AND a.doctor.doctorId = :doctorId")
    List<Appointment> findByVisitDateAndDoctorId(@Param("date") LocalDate date, @Param("doctorId") Long doctorId);


    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.patient p WHERE a.visitDate = :date")
    List<Appointment> findByDateWithDoctorAndPatient(@Param("date") LocalDate date);


    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND a.visitDate = :date AND " +
            "a.visitId <> :excludeId AND " +
            "((:startTime BETWEEN a.startTime AND a.endTime) OR " +
            "(:endTime BETWEEN a.startTime AND a.endTime) OR " +
            "(a.startTime BETWEEN :startTime AND :endTime))")
    List<Appointment> findOverlappingAppointmentsWithExclusion(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );

}
