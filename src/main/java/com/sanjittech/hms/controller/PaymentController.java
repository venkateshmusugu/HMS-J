package com.sanjittech.hms.controller;

import com.razorpay.Utils;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.RazorpayOrder;
import com.sanjittech.hms.repository.HospitalRepository;
import com.sanjittech.hms.repository.RazorpayOrderRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayOrderRepository razorpayOrderRepository;
    private final HospitalRepository hospitalRepository;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            String razorpayPaymentId = data.get("razorpayPaymentId");
            String razorpayOrderId = data.get("razorpayOrderId");
            String razorpaySignature = data.get("razorpaySignature");

            String generatedSignature = Utils.getHash(
                    razorpayOrderId + "|" + razorpayPaymentId,
                    "B4wt97kbpmr7DYE0rk81VJ1v" // TODO: move to application.properties
            );

            if (!generatedSignature.equals(razorpaySignature)) {
                return ResponseEntity.status(400).body("Invalid signature");
            }

            RazorpayOrder order = razorpayOrderRepository.findByRazorpayOrderId(razorpayOrderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            if (!order.isPaid()) {
                order.setPaid(true);
                razorpayOrderRepository.save(order);

                JSONObject notes = new JSONObject(order.getNotes());

                System.out.println("notes: " + notes);
                System.out.println("hospitalName in notes: " + notes.optString("name"));

                Hospital hospital = new Hospital();
                hospital.setName(notes.optString("name"));
                hospital.setEmail(order.getAdminEmail());
                hospital.setContactNumber("N/A");
                hospital.setAddress(notes.optString("address"));

                hospitalRepository.save(hospital);
            }

            return ResponseEntity.ok("Payment verified and hospital registered");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Verification error: " + e.getMessage());
        }
    }
}
