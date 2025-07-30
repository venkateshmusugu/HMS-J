//package com.sanjittech.hms.controller;
//
//import com.sanjittech.hms.dto.LicenseRequest;
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.checkout.Session;
//import com.stripe.param.checkout.SessionCreateParams;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/stripe")
//@CrossOrigin(origins = "*")
//public class StripeController {
//
//    @Value("${stripe.api.key}")
//    private String stripeApiKey;
//
//    @PostMapping("/create-checkout-session")
//    public ResponseEntity<?> createCheckoutSession(@RequestBody LicenseRequest request) {
//        Stripe.apiKey = stripeApiKey;
//
//        Long hospitalId = request.getHospitalId();
//        String plan = request.getPlan();
//
//        if (hospitalId == null || plan == null) {
//            return ResponseEntity.badRequest().body("Missing hospitalId or plan.");
//        }
//
//        long amount = switch (plan.toUpperCase()) {
//            case "THREE_MONTHS" -> 5000L;   // ₹50.00
//            case "SIX_MONTHS"   -> 10000L;  // ₹100.00
//            case "ONE_YEAR"     -> 18000L;  // ₹180.00
//            default -> throw new IllegalArgumentException("Invalid plan selected: " + plan);
//        };
//
//        try {
//            String successUrl = "http://localhost:3002/payment-success?hospitalId=" + hospitalId + "&plan=" + plan;
//            String cancelUrl = "http://localhost:3002/payment-cancelled";
//
//            SessionCreateParams params = SessionCreateParams.builder()
//                    .setMode(SessionCreateParams.Mode.PAYMENT)
//                    .setSuccessUrl(successUrl)
//                    .setCancelUrl(cancelUrl)
//                    .addLineItem(
//                            SessionCreateParams.LineItem.builder()
//                                    .setQuantity(1L)
//                                    .setPriceData(
//                                            SessionCreateParams.LineItem.PriceData.builder()
//                                                    .setCurrency("inr")
//                                                    .setUnitAmount(amount)
//                                                    .setProductData(
//                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
//                                                                    .setName("Hospital License - " + plan)
//                                                                    .build()
//                                                    )
//                                                    .build()
//                                    )
//                                    .build()
//                    )
//                    .build();
//
//            Session session = Session.create(params);
//            return ResponseEntity.ok(Map.of("url", session.getUrl()));
//
//        } catch (StripeException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Stripe error: " + e.getMessage());
//        }
//    }
//}
