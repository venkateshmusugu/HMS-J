package com.sanjittech.hms.util;

public class PaymentUtils {

    public static Long extractAmount(Object amountObj) {
        if (amountObj instanceof Integer) {
            return ((Integer) amountObj).longValue();
        } else if (amountObj instanceof Long) {
            return (Long) amountObj;
        } else if (amountObj instanceof String) {
            try {
                return Long.parseLong((String) amountObj);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount string: " + amountObj);
            }
        } else {
            throw new IllegalArgumentException("Unsupported amount type: " + amountObj.getClass());
        }
    }


}
