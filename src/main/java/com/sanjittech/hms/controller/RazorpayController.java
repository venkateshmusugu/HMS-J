//package com.sanjittech.hms.controller;
//
//import com.razorpay.Order;
//import com.sanjittech.hms.dto.OrderRequestDto;
//import com.sanjittech.hms.model.RazorpayOrder;
//import com.sanjittech.hms.model.RazorpayOrder;
//import com.sanjittech.hms.service.RazorpayService;
//import org.json.JSONObject;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/razorpay")
//public class RazorpayController {
//
//    private final RazorpayService razorpayService;
//
//    public RazorpayController(RazorpayService razorpayService) {
//        this.razorpayService = razorpayService;
//    }
//
//    @PostMapping("/order")
//    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto dto) {
//        try {
//            Map<String, String> notes = new HashMap<>();
//            notes.put("hospitalId", String.valueOf(dto.getHospitalId()));
//            notes.put("adminEmail", dto.getAdminEmail());
//
//            // STEP 1: Create order in Razorpay
//            Order razorpayOrder = razorpayService.createOrder(dto.getAmount(), "HMS", notes);
//
//            // STEP 2: Convert and save response in MySQL
//            JSONObject responseJson = new JSONObject(razorpayOrder.toString());
//            RazorpayOrder saved = razorpayService.saveOrderFromRazorpayResponse(responseJson);
//
//            return ResponseEntity.ok(saved); // Return saved DB entity (you can customize)
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Order creation failed: " + e.getMessage());
//        }
//    }
//}
