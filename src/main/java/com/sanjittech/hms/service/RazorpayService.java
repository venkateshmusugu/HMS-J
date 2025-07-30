package com.sanjittech.hms.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.RazorpayOrder;
import com.sanjittech.hms.repository.HospitalRepository;
import com.sanjittech.hms.repository.RazorpayOrderRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private final RazorpayOrderRepository razorpayOrderRepository;
    private final HospitalRepository hospitalRepository;

    public RazorpayService(RazorpayOrderRepository razorpayOrderRepository, HospitalRepository hospitalRepository) {
        this.razorpayOrderRepository = razorpayOrderRepository;
        this.hospitalRepository = hospitalRepository;
    }

    public Order createOrder(Long amountInPaise, String prefix, Map<String, String> metadata) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", prefix + "_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", true);
        orderRequest.put("notes", metadata); // Custom fields

        return razorpay.orders.create(orderRequest);
    }

    public RazorpayOrder saveOrderFromRazorpayResponse(JSONObject response) {
        RazorpayOrder order = new RazorpayOrder();

        order.setRazorpayOrderId(response.getString("id"));
        order.setReceiptId(response.getString("receipt"));
        order.setAmount(response.getInt("amount"));
        order.setAmountPaid(response.getInt("amount_paid"));
        order.setAmountDue(response.getInt("amount_due"));
        order.setCurrency(response.getString("currency"));
        order.setStatus(response.getString("status"));
        order.setAttempts(response.getInt("attempts"));
        order.setCreatedAt(response.getLong("created_at"));

        JSONObject notes = response.getJSONObject("notes");

        // 🧾 Debug logs
        System.out.println("notes: " + notes);
        System.out.println("hospitalId in notes: " + notes.optString("hospitalId")); // Safer than getString

        String hospitalIdStr = notes.optString("hospitalId", null);
        if (hospitalIdStr == null || hospitalIdStr.isBlank()) {
            throw new IllegalArgumentException("Missing or blank hospitalId in Razorpay notes");
        }

        long hospitalId = Long.parseLong(hospitalIdStr);
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RuntimeException("Hospital not found with ID: " + hospitalId));
        order.setHospital(hospital);

        order.setAdminEmail(notes.optString("adminEmail", null));
        order.setNotes(notes.toString());
        order.setPaid(false); // Initially false, will be set true after payment confirmation

        return razorpayOrderRepository.save(order);
    }

}
