package com.sanjittech.hms.repository;

import com.sanjittech.hms.config.UserRole;
import com.sanjittech.hms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);


    boolean existsByUsername(String admin);

    long countByRole(UserRole roleEnum);

    // ✅ FIXED
    long countByRoleAndHospital_Id(UserRole roleEnum, Long hospitalId);

    // ✅ FIXED
    List<User> findByHospital_Id(Long hospitalId);
}

