package com.sanjittech.hms.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RazorpayOrderDto {

    private String razorpayOrderId;
    private String receiptId;
    private String adminEmail;
    private String plan;
    private String amountPlan;
    private Integer amount;
    private Integer amountPaid;
    private Integer amountDue;
    private String currency;
    private String status;
    private Integer attempts;
    private Long createdAt;
    private String notes;
    private boolean paid;
}