package com.sanjittech.hms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String departmentId;
    private String reasonForVisit;

    private Long doctorId;
    private Long patientId;


}
