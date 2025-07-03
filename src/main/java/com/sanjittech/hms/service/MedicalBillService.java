package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.MedicalBillSuggestion;
import com.sanjittech.hms.dto.PatientSummaryDTO;
import com.sanjittech.hms.model.MedicalBill;
import com.sanjittech.hms.model.MedicalBillEntry;
import com.sanjittech.hms.model.Medicine;
import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.repository.MedicalBillRepository;
import com.sanjittech.hms.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MedicalBillService {

    @Autowired
    private MedicalBillRepository repository;

    @Autowired
    private MedicineRepository medicineRepo;

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

                // Set amount if not given
                if (entry.getAmount() == null && entry.getIssuedQuantity() != null && entry.getIssuedQuantity() > 0) {
                    entry.setAmount(entry.getSubtotal() / entry.getIssuedQuantity());
                }

                // ✅ Save medicine from this entry
                trySaveMedicine(entry);

                // ✅ Also save from doctorLog.medicineEntries if any
                if (entry.getDoctorLog() != null && entry.getDoctorLog().getMedicineEntries() != null) {
                    for (MedicalBillEntry dEntry : entry.getDoctorLog().getMedicineEntries()) {
                        trySaveMedicine(dEntry);
                    }
                }

                // ✅ Also save from surgery.medicineEntries if any
                if (entry.getSurgery() != null && entry.getSurgery().getMedicineEntries() != null) {
                    for (MedicalBillEntry sEntry : entry.getSurgery().getMedicineEntries()) {
                        trySaveMedicine(sEntry);
                    }
                }
            }
        }

        return repository.save(bill);
    }

    public void trySaveMedicine(MedicalBillEntry entry) {
        if (entry == null || entry.getMedicineName() == null || entry.getDosage() == null) return;

        String medicineName = entry.getMedicineName().trim();
        String dosage = entry.getDosage().trim();
        Double amount = entry.getAmount();

        if (medicineName.isEmpty() || dosage.isEmpty()) return;

        boolean exists = medicineRepo.findByNameIgnoreCaseAndDosageIgnoreCase(medicineName, dosage).isPresent();
        if (!exists) {
            Medicine newMed = Medicine.builder()
                    .name(medicineName)
                    .dosage(dosage)
                    .amount(amount)
                    .build();
            medicineRepo.save(newMed);
            System.out.println("✅ Medicine saved: " + medicineName + " - " + dosage);
        }
    }





    public List<MedicalBill> getBillsByMobile(String mobile) {
        return repository.findByPatientMobile(mobile);
    }

    public List<MedicalBill> getBillsByDate(LocalDate billDate) {
        return repository.findByBillDate(billDate);
    }

    public Optional<MedicalBill> getBillById(Long billId) {
        return repository.findById(billId);
    }

    public void deleteBillById(Long billId) {
        repository.deleteById(billId);
    }

    public List<PatientSummaryDTO> getPatientSummaries() {
        List<MedicalBill> bills = repository.findAll();
        Map<String, PatientSummaryDTO> summaryMap = new HashMap<>();

        for (MedicalBill bill : bills) {
            Patient patient = bill.getPatient();
            if (patient == null) {
                System.err.println("⚠️ Skipping bill with null patient: Bill ID = " + bill.getBillId());
                continue;
            }

            String phone = patient.getPhoneNumber();
            if (!summaryMap.containsKey(phone)) {
                PatientSummaryDTO dto = new PatientSummaryDTO();
                dto.setName(patient.getPatientName());
                dto.setMobile(phone);
                dto.setDate(bill.getCreatedDate() != null ? bill.getCreatedDate().toString() : "--");
                dto.setTime(bill.getCreatedTime() != null ? bill.getCreatedTime().toString().substring(0, 5) : "--");
                dto.setBillId(bill.getBillId());
                dto.setBillCount(1);
                summaryMap.put(phone, dto);
            } else {
                PatientSummaryDTO existing = summaryMap.get(phone);
                existing.setBillCount(existing.getBillCount() + 1);
            }
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
                ))
                .collect(Collectors.toList());
    }
}
