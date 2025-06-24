package com.sanjittech.hms.dto;

import com.sanjittech.hms.model.Medicine;
import com.sanjittech.hms.model.Surgery;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@NoArgsConstructor
@Data
public class SurgeryLogDto {
    public Long surgeryLogId;
    public LocalDate surgeryDate;
    public String medication;
    public String reason;
    public String remarks;
    public String diagnosis;
    public LocalDate followUpDate;
    public List<?> medicines;

    public String patientName;
    public int patientAge;
    public String patientPhone;

    public SurgeryLogDto(Surgery surgery) {
        this.surgeryLogId = surgery.getSurgeryLogId();
        this.surgeryDate = surgery.getSurgeryDate();
        this.medication = surgery.getMedication();
        this.reason = surgery.getReason();
        this.remarks = surgery.getRemarks();
        this.diagnosis = surgery.getDiagnosis();
        this.followUpDate = surgery.getFollowUpDate();
        this.medicines = surgery.getMedicines();

        if (surgery.getPatient() != null) {
            this.patientName = surgery.getPatient().getPatientName();
            this.patientAge = surgery.getPatient().getAge();
            this.patientPhone = surgery.getPatient().getPhoneNumber();
        }
    }
}