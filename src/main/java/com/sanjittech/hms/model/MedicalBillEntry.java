package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String medicineName;
    private String dosage;
    private String frequency;
    private Integer durationInDays;

    private Double amount;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(nullable = false)
    private Integer issuedQuantity = 1;

    private String diagnosis;
    private String reason;
    private LocalDate date;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_log_id")
    @JsonIgnore
    private DoctorLog doctorLog;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "surgery_id")
    @JsonIgnore
    private SurgeryAppointment surgery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"bills", "doctorLogs"})
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "bill_id")
    @JsonIgnore  // prevent recursion during serialization
    private MedicalBill medicalBill;

    public Double getSubtotal() {
        return (amount != null && issuedQuantity != null) ? amount * issuedQuantity : 0.0;
    }

    @Override
    public String toString() {
        return "MedicalBillEntry{id=" + entryId +
                ", purpose=" + purpose +
                ", medicineName=" + medicineName +
                ", amount=" + amount +
                ", issuedQty=" + issuedQuantity + "}";
    }


}
