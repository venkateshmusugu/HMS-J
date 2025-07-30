package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.Hospital;
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
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine, @RequestParam Long hospitalId) {
        if (medicine.getName() == null || medicine.getDosage() == null || medicine.getAmount() == null || medicine.getAmount() <= 0) {
            return ResponseEntity.badRequest().body(null);
        }

        boolean exists = medicineRepo.existsByNameIgnoreCaseAndDosageIgnoreCaseAndHospital_Id(
                medicine.getName(), medicine.getDosage(), hospitalId
        );

        if (exists) {
            return ResponseEntity.status(409).body(null); // Conflict
        }

        Hospital hospital = new Hospital();
        hospital.setId(hospitalId);
        medicine.setHospital(hospital);

        return ResponseEntity.ok(medicineRepo.save(medicine));
    }


    @GetMapping("/find")
    public ResponseEntity<Medicine> findByNameAndDosage(
            @RequestParam String name,
            @RequestParam String dosage,
            @RequestParam Long hospitalId) {

        return medicineRepo.findByNameIgnoreCaseAndDosageIgnoreCaseAndHospital_Id(name, dosage, hospitalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchMedicines(
            @RequestParam String query,
            @RequestParam Long hospitalId) {
        List<Medicine> matches = medicineRepo.findByNameContainingIgnoreCaseAndHospital_Id(query, hospitalId);
        return ResponseEntity.ok(matches);
    }
}
