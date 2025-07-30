package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Surgery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id; // ✅ Use correct name everywhere

    private LocalDate surgeryDate;
    private String medication;
    private String reasonForSurgery;
    private String remarks;
    private String diagnosis;
    private LocalDate followUpDate;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"patient"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hospital hospital;





//    @OneToMany(mappedBy = "surgeryLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<MedicalBillEntry> medications;





}