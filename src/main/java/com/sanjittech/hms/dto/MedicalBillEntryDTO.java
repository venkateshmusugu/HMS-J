package com.sanjittech.hms.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalBillEntryDTO {

    private String purpose;
    private String medicineName;
    private String dosage;
    private String frequency;
    private Integer durationInDays;
    private Double amount;
    private Integer quantity;
    private Integer issuedQuantity;
    private String diagnosis;
    private String reason;
    private LocalDate date;

    private Long doctorLogId;
    private Long surgeryId;
    private Long patientId;
    private Long billId;
}
