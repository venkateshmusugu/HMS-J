package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.Medicine;
import com.sanjittech.hms.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepo;

    @PostMapping("/create")
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        if (medicine.getName() == null || medicine.getDosage() == null || medicine.getAmount() == null || medicine.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(null);  // Reject if amount is missing or invalid
        }

        boolean exists = medicineRepo.existsByNameIgnoreCaseAndDosageIgnoreCase(
                medicine.getName(), medicine.getDosage()
        );

        if (exists) {
            return ResponseEntity.status(409).body(null);  // Conflict
        }

        return ResponseEntity.ok(medicineRepo.save(medicine));
    }



    @GetMapping("/find")
    public ResponseEntity<Medicine> findByNameAndDosage(
            @RequestParam String name,
            @RequestParam String dosage) {

        System.out.println("🔍 API /find called for: " + name + " - " + dosage);

        return medicineRepo.findByNameIgnoreCaseAndDosageIgnoreCase(name, dosage)
                .map(med -> {
                    System.out.println("✅ Medicine found: " + med.getName() + " | " + med.getDosage() + " | ₹" + med.getAmount());
                    return ResponseEntity.ok(med);
                })
                .orElseGet(() -> {
                    System.out.println("❌ Medicine not found for: " + name + " - " + dosage);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchMedicines(@RequestParam String query) {
        List<Medicine> matches = medicineRepo.findByNameContainingIgnoreCase(query);
        return ResponseEntity.ok(matches);
    }




}
