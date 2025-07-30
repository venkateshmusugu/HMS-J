package com.sanjittech.hms.controller;

import com.razorpay.*;
import com.sanjittech.hms.dto.HospitalOrderDTO;
import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.RazorpayOrder;
import com.sanjittech.hms.model.User;
import com.sanjittech.hms.repository.HospitalRepository;
import com.sanjittech.hms.repository.RazorpayOrderRepository;
import com.sanjittech.hms.repository.UserRepository;
import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.util.JwtUtil;
import com.sanjittech.hms.util.LicenseUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final RazorpayClient razorpayClient;
    private final HospitalRepository hospitalRepository;
    private final RazorpayOrderRepository razorpayOrderRepository;

    public HospitalController(
            HospitalRepository hospitalRepository,
            RazorpayOrderRepository razorpayOrderRepository,
            @Value("${razorpay.key_id}") String key,
            @Value("${razorpay.key_secret}") String secret
    ) throws RazorpayException {
        this.razorpayClient = new RazorpayClient(key, secret);
        this.hospitalRepository = hospitalRepository;
        this.razorpayOrderRepository = razorpayOrderRepository;
    }

    @PostMapping(value = "/create-order", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String createRazorpayOrder(@ModelAttribute HospitalOrderDTO dto,
                                      @RequestParam(value = "icon", required = false) MultipartFile iconFile) throws RazorpayException {
        System.out.println("Received DTO: " + dto);

        // ⬇️ Upload hospital icon to local storage
        String iconUrl = null;
        if (iconFile != null && !iconFile.isEmpty()) {
            try {
                String folder = "uploads/icons/";
                String filename = UUID.randomUUID() + "_" + iconFile.getOriginalFilename();
                Path path = Paths.get(folder + filename);
                Files.createDirectories(path.getParent());
                Files.write(path, iconFile.getBytes());

                iconUrl = "/" + folder + filename;
                System.out.println("Uploaded icon to: " + iconUrl);
            } catch (Exception e) {
                throw new RuntimeException("Icon upload failed: " + e.getMessage());
            }
        }

        // ⬇️ Razorpay order creation
        JSONObject orderRequest = new JSONObject();
        int amount = 50000;
        orderRequest.put("amount", amount);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", UUID.randomUUID().toString());

        JSONObject notes = new JSONObject();
        notes.put("hospitalName", dto.getName());
        notes.put("hospitalEmail", dto.getEmail());
        notes.put("hospitalPlan", dto.getPlan());
        notes.put("hospitalContact", dto.getContactNumber());
        notes.put("hospitalAddress", dto.getAddress());
        notes.put("hospitalIconUrl", iconUrl != null ? iconUrl : ""); // set uploaded icon URL

        // 👇 Admin credentials from frontend
        notes.put("adminName", dto.getAdminName());
        notes.put("adminEmail", dto.getAdminEmail());
        notes.put("adminPassword", dto.getAdminPassword());

        orderRequest.put("notes", notes);

        Order order = razorpayClient.orders.create(orderRequest);
        System.out.println("Created Razorpay Order: " + order);

        Object createdAtObj = order.get("created_at");
        long createdAt = createdAtObj instanceof Number ? ((Number) createdAtObj).longValue() : 0;

        RazorpayOrder rpOrder = RazorpayOrder.builder()
                .razorpayOrderId(order.get("id"))
                .receiptId(order.get("receipt"))
                .adminEmail(dto.getAdminEmail())
                .plan(dto.getPlan())
                .amount(amount)
                .amountPaid(0)
                .amountDue(amount)
                .currency("INR")
                .status(order.get("status"))
                .attempts(order.get("attempts"))
                .createdAt(createdAt)
                .notes(notes.toString())
                .paid(false)
                .build();

        razorpayOrderRepository.save(rpOrder);
        return order.toString();
    }


    @PostMapping("/razorpay-callback")
    public ResponseEntity<?> razorpayCallback(@RequestBody String payload) throws Exception {
        JSONObject paymentData = new JSONObject(payload);
        JSONObject notes = paymentData.optJSONObject("notes");

        System.out.println("Payment callback notes: " + notes);

        String hospitalName = notes.optString("hospitalName");
        String hospitalEmail = notes.optString("hospitalEmail");
        String hospitalPlan = notes.optString("hospitalPlan");
        String hospitalContact = notes.optString("hospitalContact");
        String hospitalAddress = notes.optString("hospitalAddress");
        String hospitalCity = notes.optString("hospitalCity");
        String hospitalState = notes.optString("hospitalState");
        String iconUrl = notes.optString("hospitalIconUrl");
        String hospitalPincode = notes.optString("hospitalPincode");
        String hospitalCountry = notes.optString("hospitalCountry");

        String adminName = notes.optString("adminName");
        String adminEmail = notes.optString("adminEmail");
        String adminPassword = notes.optString("adminPassword");

        // ✅ Set expiry date & license key
        LocalDate expiryDate = LicenseUtil.getExpiryDate(hospitalPlan);
        String licenseKey = LicenseUtil.generateLicenseKey(hospitalEmail);

        // ✅ Save Hospital
        Hospital hospital = Hospital.builder()
                .name(hospitalName)
                .email(hospitalEmail)
                .contactNumber(hospitalContact)
                .address(hospitalAddress)
                .city(hospitalCity)
                .state(hospitalState)
                .pincode(hospitalPincode)
                .country(hospitalCountry)
                .plan(hospitalPlan)
                .iconUrl(iconUrl)
                .isActive(true)
                .paymentDone(true)
                .registrationDate(LocalDate.now())
                .expiryDate(expiryDate)
                .licenseKey(licenseKey)
                .build();

        hospital = hospitalRepository.save(hospital);

        // ✅ Update Razorpay Order
        RazorpayOrder razorpayOrder = razorpayOrderRepository
                .findByRazorpayOrderId(paymentData.getString("order_id"))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        razorpayOrder.setPaid(true);
        razorpayOrder.setHospital(hospital);
        razorpayOrder.setAmountPaid(paymentData.getInt("amount"));
        razorpayOrderRepository.save(razorpayOrder);

        // ✅ Create Admin User
        User adminUser = userRepository.findByEmail(adminEmail).orElse(null);

        if (adminUser == null) {
            adminUser = new User();
            adminUser.setUsername(adminName);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setRole(UserRole.ADMIN);
            adminUser.setHospital(hospital);
            adminUser = userRepository.save(adminUser);
            System.out.println("✅ Admin user created with email: " + adminEmail);
        } else {
            System.out.println("⚠️ Admin already exists: " + adminEmail);
        }


        String token = jwtUtil.generateToken(adminUser.getUsername(),
                adminUser.getRole().name(),
                adminUser.getHospital().getId() );


        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "hospitalId", hospital.getId(),
                "adminEmail", adminUser.getEmail(),
                "message", "Hospital and admin registered successfully"
        ));
    }

    @PostMapping("/upload-icon")
    public ResponseEntity<?> uploadHospitalIcon(@RequestParam("file") MultipartFile file) {
        try {
            String folder = "uploads/icons/";
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(folder + filename);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            return ResponseEntity.ok(Map.of("iconUrl", "/" + folder + filename));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Icon upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable Long id) {
        Optional<Hospital> hospitalOpt = hospitalRepository.findById(id);
        if (hospitalOpt.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(hospitalOpt.get());
    }


    @GetMapping("/branding")
    public ResponseEntity<?> getBranding(HttpServletRequest request) {
        Claims claims = jwtUtil.extractClaims(request);
        Long hospitalId = ((Number) claims.get("hospitalId")).longValue();  // <- get from JWT

        Optional<Hospital> hospitalOpt = hospitalRepository.findById(hospitalId);
        if (hospitalOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Hospital not found");
        }

        Hospital hospital = hospitalOpt.get();

        return ResponseEntity.ok(Map.of(
                "name", hospital.getName(),
                "iconUrl", hospital.getIconUrl()
        ));
    }
}
