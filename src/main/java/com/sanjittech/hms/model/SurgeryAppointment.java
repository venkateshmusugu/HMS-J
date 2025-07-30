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
@Table(name = "surgery_appointment")
public class SurgeryAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate surgeryDate;
    private LocalTime surgeryTime;
    private String reason;
    private String remarks;
    private LocalDate followUpDate;
    private String surgeryType;
    private String status;
    private String diagnosis;

    @ElementCollection
    @CollectionTable(name = "surgery_notes", joinColumns = @JoinColumn(name = "surgery_appointment_id"))
    @Column(name = "note")
    private List<String> note = new ArrayList<>();

    @OneToMany(mappedBy = "surgery", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"surgery"})  // Assuming MedicalBillEntry has `surgery` field
    private List<MedicalBillEntry> medicineEntries = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties({"surgeryAppointments"})
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "surgery_log_id")
    @JsonIgnoreProperties({"surgeryAppointments"})
    private Surgery surgeryLog;

    public SurgeryAppointment(Long id) {
        this.id = id;
    }
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Hospital hospital;






}
