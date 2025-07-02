//package com.sanjittech.hms.model;
//
//import jakarta.persistence.*;
//import lombok.Data;
//
//import java.time.LocalDate;
//
//@Data
//@Entity
//public class SurgeryMedication {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    private SurgeryAppointment surgeryAppointment;
//
//    private String name;
//    private String dosage;
//    private String frequency;
//    private int durationInDays;
//    private LocalDate date;
//    private String comments;
//    private String diagnosis;
//    @ManyToOne
//    @JoinColumn(name = "surgery_log_id")
//    private Surgery surgery;
//
//
//
//}
