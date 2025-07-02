package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.MedicalBillSuggestion;
import com.sanjittech.hms.model.MedicalBill;
import com.sanjittech.hms.model.MedicalBillEntry;
import com.sanjittech.hms.repository.MedicalBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class MedicalBillService {

    @Autowired
    private MedicalBillRepository repository;

    public MedicalBill saveBill(MedicalBill bill) {
        if (bill.getBillDate() == null) {
            bill.setBillDate(LocalDate.now());
        }

        if (bill.getCreatedDate() == null) {
            bill.setCreatedDate(LocalDate.now());
        }

        if (bill.getCreatedTime() == null) {
            bill.setCreatedTime(LocalTime.now());
        }

        if (bill.getEntries() != null && !bill.getEntries().isEmpty()) {
            for (MedicalBillEntry entry : bill.getEntries()) {
                entry.setMedicalBill(bill);
                entry.setPatient(bill.getPatient());
                if (entry.getQuantity() == null || entry.getQuantity() <= 0) {
                    entry.setQuantity(1);
                }
                if (entry.getIssuedQuantity() == null || entry.getIssuedQuantity() <= 0) {
                    entry.setIssuedQuantity(1);
                }
                entry.setAmount (entry.getSubtotal());
            }
        }

        return repository.save(bill);
    }

    public List<MedicalBill> getBillsByMobile(String mobile) {
        return repository.findByPatientMobile(mobile); // Assumes query method is implemented
    }

    public List<Map<String, Object>> getPatientSummaries() {
        List<MedicalBill> all = repository.findAll();
        Map<String, Map<String, Object>> summary = new LinkedHashMap<>();

        for (MedicalBill bill : all) {
            for (MedicalBillEntry entry : bill.getEntries()) {
                if (entry.getPatient() == null) continue;

                String key = entry.getPatient().getPatientName() + "-" + entry.getPatient().getPhoneNumber();

                summary.putIfAbsent(key, new HashMap<>(Map.of(
                        "name", entry.getPatient().getPatientName(),
                        "mobile", entry.getPatient().getPhoneNumber(),
                        "billCount", 0,
                        "date", bill.getCreatedDate() != null ? bill.getCreatedDate().toString() : "--",
                        "time", bill.getCreatedTime() != null ? bill.getCreatedTime().toString() : "--"
                )));

                Map<String, Object> rec = summary.get(key);
                rec.put("billCount", (int) rec.get("billCount") + 1);
            }
        }

        return new ArrayList<>(summary.values());
    }

    public List<MedicalBillSuggestion> getMedicineSuggestions() {
        List<MedicalBill> all = repository.findAll();
        Set<String> seen = new HashSet<>();
        List<MedicalBillSuggestion> suggestions = new ArrayList<>();

        for (MedicalBill bill : all) {
            for (MedicalBillEntry entry : bill.getEntries()) {
                if (entry.getMedicineName() == null || entry.getDosage() == null) continue;

                String key = entry.getMedicineName().toLowerCase() + "|" + entry.getDosage().toLowerCase();
                if (!seen.contains(key)) {
                    suggestions.add(new MedicalBillSuggestion(
                            entry.getMedicineName(),
                            entry.getDosage(),
                            entry.getAmount()
                    ));
                    seen.add(key);
                }
            }
        }

        return suggestions;
    }

    public Optional<MedicalBill> getBillById(Long billId) {
        return repository.findById(billId);
    }

    public List<MedicalBill> getBillsByDate(LocalDate billDate) {
        return repository.findByBillDate(billDate);
    }

    public void deleteBillById(Long billId) {
        repository.deleteById(billId);
    }
}
