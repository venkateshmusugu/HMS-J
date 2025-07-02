//package com.sanjittech.hms.service;
//
//import com.sanjittech.hms.model.SurgeryMedication;
//import com.sanjittech.hms.repository.SurgeryMedicationRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//public class SurgeryMedicationService {
//
//    @Autowired
//    private SurgeryMedicationRepository repository;
//
//    public SurgeryMedication save(SurgeryMedication med) {
//        return repository.save(med);
//    }
//
//    public List<SurgeryMedication> getBySurgeryAppointmentId(Long id) {
//        return repository.findBySurgeryAppointmentId(id);
//    }
//}
//
