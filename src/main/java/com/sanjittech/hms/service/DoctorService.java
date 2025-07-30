package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.DoctorDTO;
import com.sanjittech.hms.model.Department;
import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.repository.DepartmentRepository;
import com.sanjittech.hms.repository.DoctorRepository;
import com.sanjittech.hms.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DoctorService {
    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired private DoctorRepository doctorRepository;
    @Autowired private DepartmentRepository departmentRepository;

    public List<DoctorDTO> getDoctorsWithDepartment(Hospital hospital) {
        List<Object[]> results = doctorRepository.findDoctorsWithDepartmentByHospital(hospital);
        List<DoctorDTO> doctors = new ArrayList<>();

        for (Object[] result : results) {
            DoctorDTO dto = new DoctorDTO();
            dto.setDoctorName((String) result[0]);
            dto.setDepartmentName((String) result[1]);
            dto.setDoctorId((Long) result[2]);
            doctors.add(dto);
        }

        return doctors;
    }

    public long getDoctorCount(Hospital hospital) {
        return doctorRepository.countByHospital(hospital);
    }

    public Optional<String> addDoctor(String doctorName, Long departmentId, Hospital hospital) {
        Optional<Department> deptOpt = departmentRepository.findByDepartmentIdAndHospital(departmentId, hospital);
        if (deptOpt.isEmpty()) {
            return Optional.of("Invalid department ID for this hospital");
        }

        Doctor doctor = Doctor.builder()
                .doctorName(doctorName)
                .department(deptOpt.get())
                .hospital(hospital)
                .build();

        doctorRepository.save(doctor);
        return Optional.empty();
    }

    public List<DoctorDTO> getDoctorsWithDepartmentByHospitalId(Long hospitalId) {
        return hospitalRepository.findById(hospitalId)
                .map(this::getDoctorsWithDepartment)
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
    }
}
