package com.sanjittech.hms.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@Builder
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long patientId;

    private String patientName;
    private String gender;
    private String phoneNumber;
    private LocalDate registrationDate;
    @JsonProperty("age")
    private String age;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;
    private String maritalStatus;
    private String address;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"patient"})
    private PatientCase patientCase;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"patient"})
    private List<DoctorLog> doctorLogs;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MedicalBill> bills;



    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"patient"})
    private List<Treatment> treatments;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"patient", "hibernateLazyInitializer", "handler"})
    private List<Appointment> appointments;


    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"patient"})
    private List<Surgery> surgeries;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"patient"})
    private List<SurgeryAppointment> surgeryAppointments;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"patient"})
    private List<MedicalBillEntry> billEntries;




}



