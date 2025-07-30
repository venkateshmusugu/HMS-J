package com.sanjittech.hms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentDTO {
    private Long visitId;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long hospitalId;
    private String departmentId;
    private String reasonForVisit;

    private Long doctorId;
    private String doctorName;     // ✅ add
    private Long patientId;
    private String patientName;


}
