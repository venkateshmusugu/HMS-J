    package com.sanjittech.hms.service;

    import com.sanjittech.hms.config.UserRole;
    import com.sanjittech.hms.model.Hospital;
    import com.sanjittech.hms.model.User;
    import com.sanjittech.hms.repository.HospitalRepository;
    import com.sanjittech.hms.repository.UserRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.Optional;

    @Service
    public class HospitalService {

        @Autowired
        private HospitalRepository hospitalRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        public Hospital createOrRedirectHospital(Hospital hospital, String adminEmail, String adminPassword) {
            if (hospital.getName() == null || hospital.getAddress() == null) {
                throw new IllegalArgumentException("Hospital name or address cannot be null");
            }

            String trimmedName = hospital.getName().trim();
            String trimmedAddress = hospital.getAddress().trim();

            Optional<Hospital> existing = hospitalRepository.findByNameAndAddress(trimmedName, trimmedAddress);

            if (existing.isPresent()) {
                Hospital found = existing.get();
                if (found.isPaymentDone()) {
                    throw new IllegalStateException("HOSPITAL_EXISTS_AND_PAID");
                } else {
                    throw new IllegalStateException("HOSPITAL_EXISTS_NOT_PAID");
                }
            }

            hospital.setName(trimmedName);
            hospital.setAddress(trimmedAddress);

            if (userRepository.findByEmail(adminEmail).isPresent()) {
                throw new IllegalStateException("ADMIN_EMAIL_ALREADY_IN_USE");
            }

            if (adminPassword == null || adminPassword.isBlank()) {
                throw new IllegalArgumentException("Admin password cannot be empty");
            }

            Hospital saved = hospitalRepository.save(hospital);

            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(UserRole.ADMIN);
            admin.setHospital(saved);

            userRepository.save(admin);
            return saved;
        }

    }
