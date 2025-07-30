package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByName(String name);

    Optional<Hospital> findByNameAndAddress(String name, String address);

}

