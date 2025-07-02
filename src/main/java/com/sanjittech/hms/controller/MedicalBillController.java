// --- Updated Controller: MedicalBillController.java ---

package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.MedicalBillSuggestion;
import com.sanjittech.hms.model.MedicalBill;
import com.sanjittech.hms.service.MedicalBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-bills")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class MedicalBillController {

    @Autowired
    private MedicalBillService service;

    @PostMapping(value = "/create", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> create(@RequestBody MedicalBill bill) {
        System.out.println("✅ Incoming Bill Entries: " + bill.getEntries());
        MedicalBill savedBill = service.saveBill(bill);  // return the saved object
        return ResponseEntity.ok().body(Map.of("bill_id", savedBill.getBillId()));
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
    @GetMapping("/by-bill-id/{billId}")
    public ResponseEntity<MedicalBill> getBillById(@PathVariable Long billId) {
        Optional<MedicalBill> bill = service.getBillById(billId);
        return bill.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @GetMapping("/by-phone/{mobile}")
    public ResponseEntity<List<MedicalBill>> getBillsByPhone(@PathVariable String mobile) {
        return ResponseEntity.ok(service.getBillsByMobile(mobile));
    }
    @GetMapping("/by-date")
    public ResponseEntity<List<MedicalBill>> getByDate(@RequestParam("date") String date) {
        LocalDate billDate = LocalDate.parse(date);
        return ResponseEntity.ok(service.getBillsByDate(billDate));
    }




}
