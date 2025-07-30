package com.sanjittech.hms.service;


import com.sanjittech.hms.dto.LicenseRequest;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.License;
import com.sanjittech.hms.repository.HospitalRepository;
import com.sanjittech.hms.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class LicenseService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private LicenseRepository licenseRepository;

    public String activateOrRenewLicense(LicenseRequest request) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(request.getHospitalId());

        if (hospitalOpt.isEmpty()) {
            return "Invalid hospital ID.";
        }

        Hospital hospital = hospitalOpt.get();
        License license = licenseRepository.findByHospital_Id(hospital.getId());

        if (license == null) {
            license = new License();
            license.setHospital(hospital);
        }

        license.setStartDate(LocalDate.now());
        license.setPlan(request.getPlan());

        LocalDate endDate = calculateEndDate(license.getStartDate(), request.getPlan());
        license.setEndDate(endDate);
        license.setActive(true);

        licenseRepository.save(license);
        return "License activated successfully.";
    }

    private LocalDate calculateEndDate(LocalDate startDate, String plan) {
        return switch (plan.toUpperCase()) {
            case "THREE_MONTHS" -> startDate.plusMonths(3);
            case "SIX_MONTHS" -> startDate.plusMonths(6);
            case "ONE_YEAR" -> startDate.plusYears(1);
            default -> throw new IllegalArgumentException("Invalid license plan: " + plan);
        };
    }

    public boolean isLicenseValid(Long hospitalId) {
        License license = licenseRepository.findByHospital_Id(hospitalId);
        return license != null && license.isActive() && LocalDate.now().isBefore(license.getEndDate());
    }

    public void createLicense(String hospitalId, String plan) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(Long.parseLong(hospitalId));
        if (hospitalOpt.isEmpty()) {
            throw new IllegalArgumentException("Hospital ID not found: " + hospitalId);
        }

        Hospital hospital = hospitalOpt.get();
        License license = licenseRepository.findByHospital_Id(hospital.getId());

        if (license == null) {
            license = new License();
            license.setHospital(hospital);
        }

        license.setStartDate(LocalDate.now());
        license.setPlan(plan);

        LocalDate endDate = calculateEndDate(license.getStartDate(), plan);
        license.setEndDate(endDate);
        license.setActive(true);

        licenseRepository.save(license);
    }

}
