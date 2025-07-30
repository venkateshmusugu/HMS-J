package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.License;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<License, Long> {
    License findByHospital_Id(Long hospitalId);
}
