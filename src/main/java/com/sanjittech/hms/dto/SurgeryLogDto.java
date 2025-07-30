package com.sanjittech.hms.dto;

import com.sanjittech.hms.model.MedicalBillEntry;
import com.sanjittech.hms.model.Surgery;
import com.sanjittech.hms.model.SurgeryAppointment;
import lombok.Data;

import java.util.List;

@Data
public class SurgeryLogDto {
    private String date;
    private String diagnosis;
    private String reasonForSurgery;
    private List<MedicalBillEntry> medicines;

    public SurgeryLogDto(Surgery surgery) {
        if (surgery.getSurgeryDate() != null) {
            this.date = surgery.getSurgeryDate().toString();
        }
        this.diagnosis = surgery.getDiagnosis();
        this.reasonForSurgery = surgery.getReasonForSurgery();

    }

    public SurgeryLogDto(SurgeryAppointment appointment) {
        if (appointment.getSurgeryDate() != null) {
            this.date = appointment.getSurgeryDate().toString();
        }
        this.diagnosis = appointment.getDiagnosis();
        this.reasonForSurgery = appointment.getReason();
        this.medicines = appointment.getMedicineEntries(); // From your current model
    }

    public static class PaymentRequest {
        private String plan;
        private String hospitalId;

        public String getPlan() {
            return plan;
        }

        public void setPlan(String plan) {
            this.plan = plan;
        }

        public String getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(String hospitalId) {
            this.hospitalId = hospitalId;
        }
    }
}
