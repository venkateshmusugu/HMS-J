package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurgeryAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate surgeryDate;
    private LocalTime surgeryTime;

    private String surgeryType;
    private String status; // e.g. Scheduled, Completed, Cancelled

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id")
    @JsonIgnoreProperties("appointments") // Prevents Jackson infinite loop
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "surgery_log_id")
    private Surgery surgeryLog;

    public SurgeryAppointment(Long id) {
        this.id = id;


    }

}
