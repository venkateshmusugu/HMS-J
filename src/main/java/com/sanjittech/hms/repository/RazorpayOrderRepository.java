package com.sanjittech.hms.repository;


import com.sanjittech.hms.model.RazorpayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RazorpayOrderRepository extends JpaRepository<RazorpayOrder, Long> {
    boolean existsByRazorpayOrderId(String razorpayOrderId);

    Optional<RazorpayOrder> findByRazorpayOrderId(String razorpayOrderId);

    Optional<RazorpayOrder> findTopByHospitalIdOrderByCreatedAtDesc(Long hospitalId);
}
