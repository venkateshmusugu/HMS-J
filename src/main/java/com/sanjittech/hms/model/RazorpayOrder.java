package com.sanjittech.hms.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "razorpay_orders")
public class RazorpayOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razorpay_order_id", nullable = false, unique = true)
    private String razorpayOrderId;

    @Column(name = "receipt_id")
    private String receiptId;

    @Column(name = "admin_email")
    private String adminEmail;

    private String plan;
    private String amountPlan;

    private Integer amount;
    private Integer amountPaid;
    private Integer amountDue;
    private String currency;
    private String status;
    private Integer attempts;

    @Column(name = "created_at")
    private Long createdAt;

    private boolean paid;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
}
