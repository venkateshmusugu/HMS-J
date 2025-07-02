package com.sanjittech.hms.dto;

import jakarta.persistence.ElementCollection;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SurgeryAppointmentDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;

    private LocalDate surgeryDate;
    private String surgeryTime;
    private String surgeryType;
    private String status;
    private String reasonForSurgery;
    private String remarks;
    private LocalDate followUpDate;

    private String doctorName;
    private String departmentName;

    private String patientName;
    private String phoneNumber;
    private List<String> note;
}
