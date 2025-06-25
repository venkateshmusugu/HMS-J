package com.sanjittech.hms.dto;

import com.sanjittech.hms.model.Medicine;
import com.sanjittech.hms.model.Surgery;
import com.sanjittech.hms.model.SurgeryAppointment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
public class SurgeryLogDto {
    private String date;
    private String diagnosis;
    private String reasonForSurgery;
    private List<SurgeryMedicationDTO> medicines;


    public SurgeryLogDto(Surgery surgery) {
        if (surgery.getSurgeryDate() != null) {
            this.date = surgery.getSurgeryDate().toString();
        }
        this.diagnosis = surgery.getDiagnosis();
        this.reasonForSurgery = surgery.getReason();
    }

    // ✅ NEW constructor for SurgeryAppointment
    public SurgeryLogDto(SurgeryAppointment appointment) {
        if (appointment.getSurgeryDate() != null) {
            this.date = appointment.getSurgeryDate().toString();
        }
        this.diagnosis = appointment.getDiagnosis();
        this.reasonForSurgery = appointment.getReason();

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getReasonForSurgery() {
        return reasonForSurgery;
    }

    public void setReasonForSurgery(String reasonForSurgery) {
        this.reasonForSurgery = reasonForSurgery;
    }

    public List<SurgeryMedicationDTO> getMedicines() {
        return medicines;
    }

    public void setMedicines(List<SurgeryMedicationDTO> medicines) {
        this.medicines = medicines;
    }
}
