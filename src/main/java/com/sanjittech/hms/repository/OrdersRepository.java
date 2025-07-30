package com.sanjittech.hms.repository;

import com.sanjittech.hms.model.RazorpayOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<RazorpayOrder, Long> {
    Optional<RazorpayOrder> findByRazorpayOrderId(String razorpayOrderId);

}
