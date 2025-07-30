    package com.sanjittech.hms.model;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "hospital_id")
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        private Hospital hospital;


        @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL)
        @JsonIgnore
        private List<MedicalBillEntry> billEntries = new ArrayList<>();

        @Override
        public String toString() {
            return name + " (" + dosage + ")";
        }


    }
