package com.sanjittech.hms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MedicalBillSuggestion {
    private String name;
    private String dosage;
    private Double amount;
}
