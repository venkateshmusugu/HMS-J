//package com.sanjittech.hms.controller;
//
//import com.sanjittech.hms.dto.SurgeryLogDto;
//import com.stripe.Stripe;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/payment")
//@CrossOrigin(origins = "http://localhost:3002")
//public class StripePaymentController {
//
//    @Value("${stripe.secret.key}")
//    private String secretKey;
//
//    @PostMapping("/create-checkout-session")
//    public Map<String, Object> createSession(@RequestBody SurgeryLogDto.PaymentRequest request) throws Exception {
//        Stripe.apiKey = secretKey;
//
//        long amount = switch (request.getPlan()) {
//            case "THREE_MONTHS" -> 99900L;
//            case "SIX_MONTHS" -> 179900L;
//            case "ONE_YEAR" -> 299900L;
//            default -> throw new IllegalArgumentException("Invalid Plan");
//        };
//
//        SessionCreateParams params = SessionCreateParams.builder()
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl("http://localhost:3002/payment-success?hospitalId=" + request.getHospitalId() + "&plan=" + request.getPlan())
//                .setCancelUrl("http://localhost:3002/payment-failed")
//                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
//                .addLineItem(
//                        SessionCreateParams.LineItem.builder()
//                                .setQuantity(1L)
//                                .setPriceData(
//                                        SessionCreateParams.LineItem.PriceData.builder()
//                                                .setCurrency("inr")
//                                                .setUnitAmount(amount)
//                                                .setProductData(
//                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                                .setName("HMS License - " + request.getPlan())
//                                                                .build()
//                                                )
//                                                .build()
//                                )
//                                .build()
//                )
//                .build();
//
//        Session session = Session.create(params);
//
//        return Map.of("id", session.getId(), "url", session.getUrl());
//    }
//}
