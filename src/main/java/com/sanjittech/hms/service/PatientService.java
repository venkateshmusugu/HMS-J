package com.sanjittech.hms.service;

import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient savePatient(Patient patient) {
        if (patient.getRegistrationDate() == null) {
            patient.setRegistrationDate(LocalDate.now());
        }
        return patientRepository.save(patient);
    }


    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    public Patient updatePatient(Long id, Patient patient) {
        patient.setPatientId(id);
        return patientRepository.save(patient);
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    public List<Patient> getTodayRegisteredPatients() {
        return patientRepository.findByRegistrationDate(LocalDate.now());
    }

    public List<Patient> searchPatientsByNameOrMobile(String query) {
        return patientRepository.findByPatientNameContainingIgnoreCaseOrPhoneNumberContaining(query, query);
    }
}
