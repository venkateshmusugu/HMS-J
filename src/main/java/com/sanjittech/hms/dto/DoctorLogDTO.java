package com.sanjittech.hms.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DoctorLogDTO {
    private String reasonForVisit;
    private String diagnosis;
    private boolean followUpRequired;
    private LocalDate followUpDate;
    private String testType;

    private Long patientId;
    private Long doctorId;

    private List<MedicalBillEntryDTO> medicines;
}
