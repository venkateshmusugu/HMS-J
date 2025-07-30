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

    List<Appointment> findByVisitDate(LocalDate date);

    List<Appointment> findAllByHospitalIdOrderByVisitDateAsc(Long hospitalId);


    List<Appointment> findByDoctor_DoctorIdAndVisitDateAndHospital_Id(Long doctorId, LocalDate date, Long hospitalId);



    List<Appointment> findByDoctor_DoctorIdAndHospital_Id(Long doctorId, Long hospitalId);


    @Query("SELECT a FROM Appointment a WHERE a.visitDate = :date AND " +
            "(LOWER(a.patient.patientName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR a.patient.phoneNumber LIKE CONCAT('%', :searchTerm, '%'))")
    List<Appointment> findByVisitDateAndPatientNameOrMobile(
            @Param("date") LocalDate date,
            @Param("searchTerm") String searchTerm);

    @Query("SELECT a FROM Appointment a WHERE a.visitDate = :date AND a.hospital.id = :hospitalId AND " +
            "(LOWER(a.patient.patientName) LIKE %:term% OR a.patient.phoneNumber LIKE %:term%)")
    List<Appointment> findByVisitDateAndPatientNameOrMobileAndHospital_Id(@Param("date") LocalDate date,
                                                                          @Param("term") String term,
                                                                          @Param("hospitalId") Long hospitalId);

    @Query("SELECT a FROM Appointment a WHERE " +
            "a.doctor.doctorId = :doctorId AND a.visitDate = :date AND a.hospital.id = :hospitalId AND " +
            "(LOWER(a.patient.patientName) LIKE %:term% OR a.patient.phoneNumber LIKE %:term%)")
    List<Appointment> findByDoctorAndDateAndSearchAndHospital(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("term") String term,
            @Param("hospitalId") Long hospitalId);

    @Query("SELECT a FROM Appointment a " +
            "JOIN FETCH a.doctor d JOIN FETCH a.patient p " +
            "WHERE a.visitDate = :date AND a.hospital.id = :hospitalId")
    List<Appointment> findByDateWithDoctorAndPatientAndHospital_Id(@Param("date") LocalDate date,
                                                                   @Param("hospitalId") Long hospitalId);


    @Query("SELECT a FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND a.visitDate = :date AND " +
            "a.visitId <> :excludeId AND ((:startTime BETWEEN a.startTime AND a.endTime) OR " +
            "(:endTime BETWEEN a.startTime AND a.endTime) OR " +
            "(a.startTime BETWEEN :startTime AND :endTime))")
    List<Appointment> findOverlappingAppointmentsWithExclusion(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    @Query("SELECT a FROM Appointment a WHERE a.visitDate = :date AND a.doctor.doctorId = :doctorId")
    List<Appointment> findByVisitDateAndDoctorId(@Param("date") LocalDate date, @Param("doctorId") Long doctorId);

    @Query("SELECT a FROM Appointment a WHERE a.hospital.id = :hospitalId AND " +
            "(LOWER(a.patient.patientName) LIKE %:term% OR a.patient.phoneNumber LIKE %:term%)")
    List<Appointment> findByPatientNameOrMobileAndHospital_Id(@Param("term") String term,
                                                              @Param("hospitalId") Long hospitalId);

}


