    package com.sanjittech.hms.model;

    import jakarta.persistence.*;
    import lombok.*;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Medicine {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private String dosage;

        @Column(nullable = false)
        private Double amount;

        @Override
        public String toString() {
            return name + " (" + dosage + ")";
        }
    }
