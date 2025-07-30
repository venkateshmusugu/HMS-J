package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Department;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentNameIgnoreCase(String departmentName);

    Optional<Department> findByDepartmentIdAndHospital(Long departmentId, Hospital hospital);



    List<Department> findByHospital(Hospital hospital);


    Optional<Department> findByDepartmentNameIgnoreCaseAndHospital(String departmentName, Hospital hospital);
}
