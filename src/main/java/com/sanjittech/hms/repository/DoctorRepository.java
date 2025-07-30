package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("SELECT d.doctorName, dep.departmentName, d.doctorId FROM Doctor d " +
            "JOIN d.department dep")
    List<Object[]> findDoctorsWithDepartment();

    Optional<Doctor> findByUser_Username(String name);
    @Query("SELECT d.doctorName, dep.departmentName, d.doctorId FROM Doctor d " +
            "JOIN d.department dep WHERE d.hospital = :hospital")
    List<Object[]> findDoctorsWithDepartmentByHospital(Hospital hospital);

    long countByHospital(Hospital hospital);

}
