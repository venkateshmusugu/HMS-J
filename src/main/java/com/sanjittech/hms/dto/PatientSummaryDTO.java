package com.sanjittech.hms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PatientSummaryDTO {

    private Long billId;
    private String name;
    private String mobile;
    private int billCount;
    private String date;  // or LocalDate if preferred
    private String time;



}
