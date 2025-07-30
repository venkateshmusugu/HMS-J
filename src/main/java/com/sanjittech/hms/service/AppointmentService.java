package com.sanjittech.hms.service;

import com.sanjittech.hms.dto.AppointmentDTO;
import com.sanjittech.hms.model.*;
import com.sanjittech.hms.repository.AppointmentRepository;
import com.sanjittech.hms.repository.DoctorRepository;
import com.sanjittech.hms.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public Appointment saveAppointmentFromDTO(AppointmentDTO dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();
        Patient patient = patientRepository.findById(dto.getPatientId()).orElseThrow();

        if (!isSlotAvailable(dto.getVisitDate(), dto.getStartTime().toString(), dto.getEndTime().toString(), dto.getDoctorId()))
            throw new RuntimeException("Slot unavailable");
        Hospital hospital = doctor.getHospital();
        Department department = doctor.getDepartment();
        return appointmentRepository.save(Appointment.builder()
                        .visitDate(dto.getVisitDate())
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .reasonForVisit(dto.getReasonForVisit())
                        .doctor(doctor)
                        .department(department)
                        .patient(patient)
                        .status(AppointmentStatus.ACTIVE)
                        .hospital(hospital) // ✅ set hospital
                        .build()
        );
    }

    public List<AppointmentDTO> getFilteredAppointments(String searchTerm, LocalDate date, Long doctorId, Long hospitalId) {
        List<Appointment> appointments;

        if (doctorId != null && date != null && searchTerm != null) {
            appointments = appointmentRepository.findByDoctorAndDateAndSearchAndHospital(doctorId, date, searchTerm.toLowerCase(), hospitalId);
        } else if (doctorId != null && date != null) {
            appointments = appointmentRepository.findByDoctor_DoctorIdAndVisitDateAndHospital_Id(doctorId, date, hospitalId);

        } else if (doctorId != null) {
            appointments = appointmentRepository.findByDoctor_DoctorIdAndHospital_Id(doctorId, hospitalId);

        } else if (date != null && searchTerm != null) {
            appointments = appointmentRepository.findByVisitDateAndPatientNameOrMobileAndHospital_Id(date, searchTerm, hospitalId);

        } else if (searchTerm != null) {
            appointments = appointmentRepository.findByPatientNameOrMobileAndHospital_Id(searchTerm, hospitalId);

        } else if (date != null) {
            appointments = appointmentRepository.findByDateWithDoctorAndPatientAndHospital_Id(date, hospitalId);

        } else {
            appointments = appointmentRepository.findAllByHospitalIdOrderByVisitDateAsc(hospitalId);
        }

        return appointments.stream().map(this::toDTO).toList();
    }

    private AppointmentDTO toDTO(Appointment appt) {
        return AppointmentDTO.builder()
                .visitId(appt.getVisitId())
                .visitDate(appt.getVisitDate())
                .startTime(appt.getStartTime())
                .endTime(appt.getEndTime())
                .reasonForVisit(appt.getReasonForVisit())
                .patientId(appt.getPatient().getPatientId())
                .patientName(appt.getPatient().getPatientName())
                .doctorId(appt.getDoctor().getDoctorId())
                .doctorName(appt.getDoctor().getDoctorName())
                .hospitalId(appt.getHospital().getId())
                .departmentId(String.valueOf(appt.getDoctor().getDepartment().getDepartmentId()))
                .build();
    }

    public boolean isSlotAvailable(LocalDate date, String startTime, String endTime, Long doctorId) {
        LocalTime start = LocalTime.parse(startTime);
        LocalTime end = LocalTime.parse(endTime);
        List<Appointment> existing = appointmentRepository.findByVisitDateAndDoctorId(date, doctorId);
        return existing.stream().noneMatch(a -> start.isBefore(a.getEndTime()) && end.isAfter(a.getStartTime()));
    }

    public Appointment updateAppointment(Long id, Appointment appointment) {
        Appointment existing = appointmentRepository.findById(id).orElseThrow();
        existing.setVisitDate(appointment.getVisitDate());
        existing.setStartTime(appointment.getStartTime());
        existing.setEndTime(appointment.getEndTime());
        existing.setReasonForVisit(appointment.getReasonForVisit());

        Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId()).orElseThrow();
        Patient patient = patientRepository.findById(appointment.getPatient().getPatientId()).orElseThrow();

        existing.setDoctor(doctor);
        existing.setPatient(patient);
        return appointmentRepository.save(existing);
    }

    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElseThrow();
    }

    public Appointment bookAppointmentWithPatientId(Long patientId, AppointmentDTO dto) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        Doctor doctor = doctorRepository.findById(dto.getDoctorId()).orElseThrow();
        Hospital hospital = doctor.getHospital();

        return appointmentRepository.save(
                Appointment.builder()
                        .visitDate(dto.getVisitDate())
                        .startTime(dto.getStartTime())
                        .endTime(dto.getEndTime())
                        .reasonForVisit(dto.getReasonForVisit())
                        .doctor(doctor)
                        .patient(patient)
                        .hospital(hospital)
                        .status(AppointmentStatus.ACTIVE)
                        .build()
        );
    }

    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    public ResponseEntity<String> cancelAppointment(Long id) {
        Appointment appt = appointmentRepository.findById(id).orElse(null);
        if (appt != null) {
            appt.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(appt);
            return ResponseEntity.ok("Appointment cancelled.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found.");
    }
}
