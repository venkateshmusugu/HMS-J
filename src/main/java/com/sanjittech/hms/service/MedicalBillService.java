package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.MedicalBillSuggestion;
import com.sanjittech.hms.dto.PatientSummaryDTO;
import com.sanjittech.hms.model.MedicalBill;
import com.sanjittech.hms.model.MedicalBillEntry;
import com.sanjittech.hms.model.Medicine;
import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.repository.MedicalBillEntryRepository;
import com.sanjittech.hms.repository.MedicalBillRepository;
import com.sanjittech.hms.repository.MedicineRepository;
import com.sanjittech.hms.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicalBillService {

    @Autowired
    private MedicalBillRepository billRepo;

    @Autowired
    private MedicalBillEntryRepository entryRepo;



    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private MedicineRepository medicineRepo;

    public MedicalBill saveBill(MedicalBill bill) {
        if (bill.getBillDate() == null) bill.setBillDate(LocalDate.now());
        if (bill.getCreatedDate() == null) bill.setCreatedDate(LocalDate.now());
        if (bill.getCreatedTime() == null) bill.setCreatedTime(LocalTime.now());

        if (bill.getEntries() != null && !bill.getEntries().isEmpty()) {
            Set<String> uniqueMedKeys = new HashSet<>();

            for (MedicalBillEntry entry : bill.getEntries()) {
                entry.setMedicalBill(bill);
                entry.setPatient(bill.getPatient());

                if (entry.getQuantity() == null || entry.getQuantity() <= 0)
                    entry.setQuantity(1);

                if (entry.getIssuedQuantity() == null || entry.getIssuedQuantity() <= 0)
                    entry.setIssuedQuantity(1);

                Medicine medicine = entry.getMedicine();
                if (medicine == null) {
                    throw new RuntimeException("Medicine is missing in bill entry");
                }

                String name = medicine.getName() != null ? medicine.getName().trim() : null;
                String dosage = medicine.getDosage() != null ? medicine.getDosage().trim() : null;
                Double amount = medicine.getAmount();

                if (name == null || dosage == null || amount == null) {
                    throw new RuntimeException("Medicine is incomplete: " + medicine);
                }

                String medKey = (name + "|" + dosage).toLowerCase();
                if (!uniqueMedKeys.add(medKey)) {
                    throw new RuntimeException("Duplicate medicine entry: " + name + " " + dosage);
                }

                Optional<Medicine> existing = medicineRepo.findByNameIgnoreCaseAndDosageIgnoreCase(name, dosage);

                Medicine medToUse = existing.orElseGet(() -> {
                    medicine.setName(name);
                    medicine.setDosage(dosage);
                    return medicineRepo.save(medicine);
                });

                entry.setMedicine(medToUse);
                entry.setSubtotal(medToUse.getAmount() * entry.getIssuedQuantity());

                // ✅ Set transient fields
                entry.setMedicineName(medToUse.getName());
                entry.setDosage(medToUse.getDosage());
                entry.setAmount(medToUse.getAmount());
            }
        }

        return billRepo.save(bill);
    }



    public void trySaveMedicine(Medicine medicine) {
        if (medicine == null) return;

        String name = medicine.getName();
        String dosage = medicine.getDosage();
        Double amount = medicine.getAmount();

        if (name == null || dosage == null || name.trim().isEmpty() || dosage.trim().isEmpty()) return;

        boolean exists = medicineRepo.findByNameIgnoreCaseAndDosageIgnoreCase(name.trim(), dosage.trim()).isPresent();
        if (!exists) {
            medicineRepo.save(medicine);
            System.out.println("✅ New medicine saved: " + name + " (" + dosage + ")");
        }
    }

    public List<MedicalBill> getBillsByMobile(String mobile) {
        List<MedicalBill> bills = billRepo.findByPatientMobile(mobile);

        for (MedicalBill bill : bills) {
            if (bill.getEntries() != null) {
                for (MedicalBillEntry entry : bill.getEntries()) {
                    Medicine med = entry.getMedicine();
                    if (med != null) {
                        entry.setMedicineName(med.getName());
                        entry.setDosage(med.getDosage());
                        entry.setAmount(med.getAmount());
                        if (entry.getSubtotal() == null) {
                            entry.setSubtotal(med.getAmount() * entry.getIssuedQuantity());
                        }
                    }
                }
            }
        }

        return bills;
    }


    public List<MedicalBill> getBillsByDate(LocalDate date) {
        System.out.println("🔍 Looking for bills on: " + date);
        List<MedicalBill> bills = Optional.ofNullable(billRepo.findByBillDate(date)).orElse(Collections.emptyList());
        System.out.println("✅ Found " + bills.size() + " bills for date " + date);

        for (MedicalBill bill : bills) {
            for (MedicalBillEntry entry : bill.getEntries()) {
                Medicine med = entry.getMedicine();
                if (med != null) {
                    entry.setMedicineName(med.getName());
                    entry.setDosage(med.getDosage());
                    entry.setAmount(med.getAmount());
                    if (entry.getSubtotal() == null) {
                        entry.setSubtotal(med.getAmount() * entry.getIssuedQuantity());
                    }
                }
            }
        }

        return bills;
    }



    public Optional<MedicalBill> getBillById(Long id) {
        return billRepo.findById(id);
    }

    public void deleteBillById(Long id) {
        billRepo.deleteById(id);
    }

    public List<PatientSummaryDTO> getPatientSummaries() {
        List<MedicalBill> bills = billRepo.findAll();
        Map<String, PatientSummaryDTO> summaryMap = new HashMap<>();

        for (MedicalBill bill : bills) {
            Patient patient = bill.getPatient();
            if (patient == null) continue;

            String phone = patient.getPhoneNumber();
            summaryMap.compute(phone, (k, v) -> {
                if (v == null) {
                    return PatientSummaryDTO.builder()
                            .name(patient.getPatientName())
                            .mobile(phone)
                            .billId(bill.getBillId())
                            .billCount(1)
                            .date(bill.getCreatedDate() + "")
                            .time(bill.getCreatedTime() + "")
                            .build();
                } else {
                    v.setBillCount(v.getBillCount() + 1);
                    return v;
                }
            });
        }

        return new ArrayList<>(summaryMap.values());
    }

    public List<MedicalBillSuggestion> getMedicineSuggestions() {
        return medicineRepo.findAll()
                .stream()
                .map(med -> new MedicalBillSuggestion(
                        med.getName(),
                        med.getDosage(),
                        med.getAmount()
                )).collect(Collectors.toList());
    }

    public MedicalBill createBillFromEntryIds(Long patientId, List<Long> entryIds) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        // ✅ FIX: Get entries from MedicalBillEntryRepository, not billRepo
        // ✅ CORRECT: Fetching from MedicalBillEntryRepository (entryRepo)
        List<MedicalBillEntry> entries = entryRepo.findAllById(entryIds);



        // Validate all entries
        for (MedicalBillEntry entry : entries) {
            if (entry.getMedicalBill() != null) {
                throw new RuntimeException("Entry already billed: ID " + entry.getEntryId());
            }

            entry.setPatient(patient); // optional if not already set
        }

        MedicalBill bill = new MedicalBill();
        bill.setPatient(patient);
        bill.setBillDate(LocalDate.now());
        bill.setCreatedDate(LocalDate.now());
        bill.setCreatedTime(LocalTime.now());
        bill.setEntries(entries);

        for (MedicalBillEntry entry : entries) {
            entry.setMedicalBill(bill);
            entry.setSubtotal(entry.getMedicine().getAmount() * entry.getIssuedQuantity());
        }

        return billRepo.save(bill);
    }


    public MedicalBill getOrCreateOpenBillForPatient(Patient patient) {
        return billRepo.findByPatientAndStatus(patient, "OPEN")
                .orElseGet(() -> {
                    MedicalBill newBill = new MedicalBill();
                    newBill.setPatient(patient);
                    newBill.setStatus("OPEN");
                    newBill.setCreatedDate(LocalDate.now());
                    return billRepo.save(newBill);
                });
    }

}
