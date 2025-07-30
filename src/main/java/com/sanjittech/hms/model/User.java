    package com.sanjittech.hms.model;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.sanjittech.hms.config.UserRole;
    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.ToString;

    @Setter
    @Getter
    @Entity
    @NoArgsConstructor
    @Table(name = "users")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ToString(exclude = "password")
    public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String username;

        private String password;

        @Column(unique = true)
        private String email;

        @Enumerated(EnumType.STRING)
        private UserRole role;

        // ✅ Add this field to assign user to a specific hospital
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "hospital_id")
        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        private Hospital hospital;

        @Transient
        private Long hospitalId;

        public Long getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(Long hospitalId) {
            this.hospitalId = hospitalId;
        }
    }
