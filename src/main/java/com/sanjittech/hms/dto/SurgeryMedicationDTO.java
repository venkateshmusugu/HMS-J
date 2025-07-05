 package com.sanjittech.hms.dto;

 import com.sanjittech.hms.model.MedicalBillEntry;
 import jakarta.persistence.Entity;
 import lombok.Data;

 import java.util.List;

 @Data

 public class SurgeryMedicationDTO {
    private String diagnosis;
    private String reasonForSurgery;
    private String followUpDate;
    private List<MedicalBillEntry> medicines;


}