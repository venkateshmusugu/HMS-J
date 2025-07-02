package com.sanjittech.hms.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {
    private String patientName;
    private String gender;
    private String phoneNumber;
    private String age;
    private LocalDate dob;
    private String maritalStatus;
    private String caseDescription;
}

