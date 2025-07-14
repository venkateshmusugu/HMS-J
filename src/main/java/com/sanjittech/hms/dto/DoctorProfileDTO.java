package com.sanjittech.hms.dto;

import lombok.Data;

@Data
public class DoctorProfileDTO {
    private String doctorName;
    private String departmentName;

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String  getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public DoctorProfileDTO(String departmentName) {

        this.departmentName = departmentName;
    }
}
