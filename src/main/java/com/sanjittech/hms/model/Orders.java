//package com.sanjittech.hms.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Orders {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "razorpay_order_id", nullable = false, unique = true)
//    private String razorpayOrderId;
//
//    private String plan;
//    private String amountPlan;
//    private boolean paid;
//
//    @ManyToOne
//    @JoinColumn(name = "hospital_id", nullable = false)
//    private Hospital hospital;
//}
