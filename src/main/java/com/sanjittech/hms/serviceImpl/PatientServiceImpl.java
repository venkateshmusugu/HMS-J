//package com.sanjittech.hms.serviceImpl;
//
//import com.sanjittech.hms.model.Patient;
//import com.sanjittech.hms.repository.PatientRepository;
//import com.sanjittech.hms.service.PatientService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class PatientServiceImpl extends PatientService {
//
//    @Autowired
//    private PatientRepository patientRepository;
//
//    @Override
//    public Patient savePatient(Patient patient) {
//        return patientRepository.save(patient);
//    }
//
//    @Override
//    public List<Patient> getAllPatients() {
//        return patientRepository.findAll();
//    }
//
//    @Override
//    public Patient getPatientById(Long id) {
//        return patientRepository.findById(id).orElse(null);
//    }
//
//    @Override
//    public Patient updatePatient(Long id, Patient patient) {
//        Optional<Patient> existingPatient = patientRepository.findById(id);
//        if (existingPatient.isPresent()) {
//            patient.setPatientId(id);
//            return patientRepository.save(patient);
//        } else {
//            return null;
//        }
//    }
//
//    @Override
//    public void deletePatient(Long id) {
//        patientRepository.deleteById(id);
//    }
//}
