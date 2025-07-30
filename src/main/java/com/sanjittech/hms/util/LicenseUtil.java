package com.sanjittech.hms.util;

import java.time.LocalDate;
import java.util.Base64;

public class LicenseUtil {
    public static String generateLicenseKey(String email) {
//        return Base64.getEncoder().encodeToString((email + ":" + System.currentTimeMillis()).getBytes());

        String licenseKey = Base64.getEncoder().encodeToString((email + ":" + System.currentTimeMillis()).getBytes());
        System.out.println("🔐 Generated License Key: " + licenseKey);
        return licenseKey;

    }

    public static LocalDate getExpiryDate(String plan) {
        return switch (plan.toUpperCase()) {
            case "BASIC" -> LocalDate.now().plusMonths(3);
            case "STANDARD" -> LocalDate.now().plusMonths(6);
            case "PREMIUM" -> LocalDate.now().plusYears(1);
            default -> throw new IllegalArgumentException("Invalid plan: " + plan);
        };
    }
}
