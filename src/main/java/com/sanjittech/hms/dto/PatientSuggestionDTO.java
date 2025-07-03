// PatientSuggestionDTO.java
package com.sanjittech.hms.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSuggestionDTO {
    private Long patientId;
    private String patientName;
    private String phoneNumber;
}
