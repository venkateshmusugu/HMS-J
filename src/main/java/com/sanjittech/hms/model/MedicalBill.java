package com.sanjittech.hms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    private String medicineName;
    private String dosage;
    private int quantity;
    private Double amount;
    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Patient patient;

    @ManyToOne
    private Medicine medicine;
}
