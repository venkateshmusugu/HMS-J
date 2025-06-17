package com.sanjittech.hms.dto;

import jakarta.persistence.Entity;




    public class MedicineDTO {
        private String name;
        private String dosage;
        private String frequency;
        private int durationInDays;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public int getDurationInDays() {
            return durationInDays;
        }

        public void setDurationInDays(int durationInDays) {
            this.durationInDays = durationInDays;
        }
    }


