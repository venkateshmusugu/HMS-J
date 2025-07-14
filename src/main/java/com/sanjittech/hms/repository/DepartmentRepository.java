package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentNameIgnoreCase(String departmentName);
}
