    package com.sanjittech.hms.dto;

    public class OrderRequestDto {
        private Long amount;
        private Long hospitalId;
        private String adminEmail;

        // Getters and Setters
        public Long getAmount() {
            return amount;
        }
        public void setAmount(long amount) {
            this.amount = amount;
        }
        public Long getHospitalId() {
            return hospitalId;
        }
        public void setHospitalId(Long hospitalId) {
            this.hospitalId = hospitalId;
        }
        public String getAdminEmail() {
            return adminEmail;
        }
        public void setAdminEmail(String adminEmail) {
            this.adminEmail = adminEmail;
        }
    }
