package com.sanjittech.hms.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 public class MedicineDTO {
     private String name;
     private String dosage;
     private String frequency;


 }

