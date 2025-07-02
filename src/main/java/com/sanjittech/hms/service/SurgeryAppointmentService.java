package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.SurgeryAppointmentDTO;
import com.sanjittech.hms.model.Doctor;
import com.sanjittech.hms.model.Patient;
import com.sanjittech.hms.model.SurgeryAppointment;
import com.sanjittech.hms.repository.DoctorRepository;
import com.sanjittech.hms.repository.PatientRepository;
import com.sanjittech.hms.repository.SurgeryAppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SurgeryAppointmentService {

    @Autowired
    private SurgeryAppointmentRepository appointmentRepo;

    @Autowired
    private PatientRepository patientRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    public SurgeryAppointment bookAppointment(Long patientId, SurgeryAppointmentDTO dto) {
        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepo.findById(dto.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        SurgeryAppointment appointment = new SurgeryAppointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSurgeryDate(dto.getSurgeryDate());
        appointment.setSurgeryTime(LocalTime.parse(dto.getSurgeryTime()));
        appointment.setSurgeryType(dto.getSurgeryType());
        appointment.setStatus(dto.getStatus());
        appointment.setReason(dto.getReasonForSurgery());
        appointment.setRemarks(dto.getRemarks());
        appointment.setFollowUpDate(dto.getFollowUpDate());
        appointment.setNote(dto.getNote());

        return appointmentRepo.save(appointment);
    }

    public List<SurgeryAppointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepo.findBySurgeryDate(date);
    }

    public List<SurgeryAppointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepo.findByPatientPatientId(patientId);
    }

    public void deleteAppointment(Long id) {
        appointmentRepo.deleteById(id);
    }

    public SurgeryAppointment getById(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public SurgeryAppointment updateAppointment(Long id, SurgeryAppointmentDTO dto) {
        SurgeryAppointment existing = getById(id);

        existing.setSurgeryDate(dto.getSurgeryDate());
        existing.setSurgeryTime(LocalTime.parse(dto.getSurgeryTime()));
        existing.setSurgeryType(dto.getSurgeryType());
        existing.setStatus(dto.getStatus());
        existing.setReason(dto.getReasonForSurgery());
        existing.setRemarks(dto.getRemarks());
        existing.setFollowUpDate(dto.getFollowUpDate());
        existing.setNote(dto.getNote());

        return appointmentRepo.save(existing);
    }

    public List<SurgeryAppointmentDTO> getAppointmentsWithDoctorPatientNote(LocalDate date) {
        List<SurgeryAppointment> appointments = appointmentRepo.findBySurgeryDate(date);
        return appointments.stream().map(app -> {
            SurgeryAppointmentDTO dto = new SurgeryAppointmentDTO();
            dto.setId(app.getId());
            dto.setSurgeryDate(app.getSurgeryDate());
            dto.setSurgeryTime(app.getSurgeryTime().toString());
            dto.setSurgeryType(app.getSurgeryType());
            dto.setStatus(app.getStatus());
            dto.setReasonForSurgery(app.getReason());
            dto.setRemarks(app.getRemarks());
            dto.setFollowUpDate(app.getFollowUpDate());
            dto.setNote(app.getNote());

            if (app.getDoctor() != null) {
                dto.setDoctorId(app.getDoctor().getDoctorId());
                dto.setDoctorName(app.getDoctor().getDoctorName());
                dto.setDepartmentName(app.getDoctor().getDepartment() != null
                        ? app.getDoctor().getDepartment().getDepartmentName() : null);
            }

            if (app.getPatient() != null) {
                dto.setPatientId(app.getPatient().getPatientId());
                dto.setPatientName(app.getPatient().getPatientName());
                dto.setPhoneNumber(app.getPatient().getPhoneNumber());
            }

            return dto;
        }).collect(Collectors.toList());
    }
}
