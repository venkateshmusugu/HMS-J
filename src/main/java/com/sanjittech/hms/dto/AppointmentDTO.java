package com.sanjittech.hms.dto;

import com.sanjittech.hms.model.Appointment;

public class AppointmentDTO {
    private Long visitId;
    private String patientName;
    private String doctorName;

    public AppointmentDTO(Appointment a) {
        this.visitId = a.getVisitId();
        this.patientName = a.getPatient() != null ? a.getPatient().getPatientName() : null;
        this.doctorName = a.getDoctor() != null ? a.getDoctor().getDoctorName() : null;
    }

}
