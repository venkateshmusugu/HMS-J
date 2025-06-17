package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    @Query("SELECT d.doctorName, dep.departmentName, d.doctorId FROM Doctor d " +
            "JOIN d.department dep")
    List<Object[]> findDoctorsWithDepartment();
}
