package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "bill_id", nullable = true)
    private Long billId;
    private String status;
    private LocalDate billDate;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "created_time")
    private LocalTime createdTime;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    @JsonIgnoreProperties({"bills", "doctorLogs"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hospital hospital;


    @OneToMany(mappedBy = "medicalBill", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("medicalBill")
    private List<MedicalBillEntry> entries = new ArrayList<>();

    public Double getTotalAmount() {
        return entries.stream()
                .mapToDouble(MedicalBillEntry::getSubtotal)
                .sum();
    }

    @Override
    public String toString() {
        return "MedicalBill{" +
                "billId=" + billId +
                ", billDate=" + billDate +
                ", patientId=" + (patient != null ? patient.getPatientId() : null) +
                ", totalEntries=" + entries.size() +
                '}';
    }
}
