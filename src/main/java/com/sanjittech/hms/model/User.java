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
@ToString(exclude = "password") // <--- ADD THIS LINE!
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password; // This field should be excluded from toString()

    @Enumerated(EnumType.STRING)
    private UserRole role;

}