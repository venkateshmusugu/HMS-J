package com.sanjittech.hms.config;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorpayConfig {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    @Value("${test.property}")
    private String test;




    //    @Bean
//    public RazorpayClient razorpayClient() throws Exception {
//        return new RazorpayClient(keyId, keySecret);
//    }
   @Bean
    public RazorpayClient getClient() throws Exception {
       System.out.println("🔑 Razorpay Key ID: " + keyId);
       System.out.println("🗝️ Razorpay Secret: " + keySecret);
        return new RazorpayClient(keyId, keySecret);
    }
}
