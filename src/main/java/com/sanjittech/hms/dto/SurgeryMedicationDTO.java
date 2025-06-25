package com.sanjittech.hms.dto;

import jakarta.persistence.Column;

import java.time.LocalDate;

public class SurgeryMedicationDTO {
    @Column(name = "diagnosis")
    public String diagnosis;
    private String medicineName;
    private String dosage;
    private String duration;
    private String comments;

    // Constructors
    public SurgeryMedicationDTO(String name, String dosage, String duration, String comments,String diagnosis) {
        this.medicineName = name;
        this.dosage = dosage;
        this.duration = duration;
        this.comments = comments;
        this.diagnosis= diagnosis;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}