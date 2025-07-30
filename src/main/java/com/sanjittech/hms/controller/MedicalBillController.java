    // --- Updated Controller: MedicalBillController.java ---

    package com.sanjittech.hms.controller;

    import com.sanjittech.hms.dto.MedicalBillSuggestion;
    import com.sanjittech.hms.dto.PatientSuggestionDTO;

    import com.sanjittech.hms.model.MedicalBill;
    import com.sanjittech.hms.model.Patient;
    import com.sanjittech.hms.repository.MedicalBillRepository;
    import com.sanjittech.hms.repository.PatientRepository;
    import com.sanjittech.hms.service.MedicalBillService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.ModelAndView;

    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @RestController
    @RequestMapping("/api/medical-bills")
    @CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
    public class MedicalBillController {



        @Autowired
        private MedicalBillService service;

        @Autowired
        private PatientRepository patientRepo;

        // ✅ SIMPLIFIED POST: Delegate everything to service
        @PostMapping(
                value = "/create",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE
        )
        public ResponseEntity<?> create(@RequestBody MedicalBill bill, @RequestParam String phone) {
            Patient patient = patientRepo.findByPhoneNumber(phone);
            if (patient == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Patient not found for phone: " + phone);
            }

            bill.setPatient(patient);
            MedicalBill saved = service.saveBill(bill);  // ✅ handles all logic now
            return ResponseEntity.ok(saved);
        }

        @DeleteMapping("/delete/{billId}")
        public ResponseEntity<?> deleteBill(@PathVariable Long billId) {
            try {
                service.deleteBillById(billId);
                return ResponseEntity.ok("Deleted successfully");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Error deleting bill: " + e.getMessage());
            }
        }

        @GetMapping("/suggestions")
        public ResponseEntity<List<MedicalBillSuggestion>> getSuggestions() {
            return ResponseEntity.ok(service.getMedicineSuggestions());
        }

        @GetMapping("/patient-summary")
        public ResponseEntity<?> getPatientSummaries() {
            return ResponseEntity.ok(service.getPatientSummaries());
        }

        @GetMapping("/by-mobile/{mobile}")
        public ResponseEntity<List<MedicalBill>> getBillsByMobile(@PathVariable String mobile) {
            return ResponseEntity.ok(service.getBillsByMobile(mobile));
        }

        @GetMapping("/by-phone/{mobile}")
        public ResponseEntity<List<MedicalBill>> getBillsByPhone(@PathVariable String mobile) {
            return ResponseEntity.ok(service.getBillsByMobile(mobile));
        }

        @GetMapping("/by-bill-id/{billId}")
        public ResponseEntity<MedicalBill> getBillById(@PathVariable Long billId) {
            return service.getBillById(billId)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        @GetMapping(value = "/by-date", produces = MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<List<MedicalBill>> getByDate(@RequestParam("date") String date) {
             System.out.println("📅 Date filter request received: " + date);
             System.out.println("🔍 Looking for bills on: " + date);

            List<MedicalBill> bills = service.getBillsByDate(LocalDate.parse(date));
            return ResponseEntity.ok(bills);
        }

    }

