package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private Long billId;

    private LocalDate billDate;




    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"bills", "doctorLogs"})
    private Patient patient;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "created_time")
    private LocalTime createdTime;

    @OneToMany(mappedBy = "medicalBill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalBillEntry> entries = new ArrayList<>();

    public Double getTotalAmount() {
        return entries.stream().mapToDouble(MedicalBillEntry::getSubtotal).sum();
    }

    @Override
    public String toString() {
        return "MedicalBill{id=" + billId + ", billDate=" + billDate + ", entries=" + entries.size() + "}";
    }
}
