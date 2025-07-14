    package com.sanjittech.hms.model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.*;

    import java.util.ArrayList;
    import java.util.List;

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

        @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL)
        @JsonIgnore
        private List<MedicalBillEntry> billEntries = new ArrayList<>();

        @Override
        public String toString() {
            return name + " (" + dosage + ")";
        }


    }
