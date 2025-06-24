package com.sanjittech.hms.dto;

import java.time.LocalDate;

public class SurgeryMedicationDTO {
    private Long id;
    private Long surgeryAppointmentId;
    private String name;
    private String dosage;
    private String frequency;
    private int durationInDays;
    private LocalDate date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSurgeryAppointmentId() {
        return surgeryAppointmentId;
    }

    public void setSurgeryAppointmentId(Long surgeryAppointmentId) {
        this.surgeryAppointmentId = surgeryAppointmentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}