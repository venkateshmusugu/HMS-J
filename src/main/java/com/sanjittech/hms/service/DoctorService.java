package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.DoctorDTO;
import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    public List<DoctorDTO> getDoctorsWithDepartment() {
        List<Object[]> results = doctorRepository.findDoctorsWithDepartment();
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
}

