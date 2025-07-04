package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalBillEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long entryId;

    private String purpose;


    private String frequency;
    private Integer durationInDays;


    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false)
    private Integer issuedQuantity = 1;

    private String diagnosis;
    private String reason;
    private LocalDate date;
    @Transient
    private String medicineName;

    @Transient
    private String dosage;

    @Transient
    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Medicine medicine;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_log_id")
    @JsonIgnore
    private DoctorLog doctorLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgery_id")
    @JsonIgnore
    private SurgeryAppointment surgery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"bills", "doctorLogs"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    @JsonIgnore
    private MedicalBill medicalBill;
    @Column
    private Double subtotal;


    public Double getSubtotal() {
        return (medicine.getAmount() != null && issuedQuantity != null) ? medicine.getAmount() * issuedQuantity : 0.0;
    }

    @Override
    public String toString() {
        return "MedicalBillEntry{" +
                "entryId=" + entryId +
                ", medicine=" + (medicine != null ? medicine.getName() + " " + medicine.getDosage() : "null") +
                ", issuedQty=" + issuedQuantity +
                '}';
    }
}
