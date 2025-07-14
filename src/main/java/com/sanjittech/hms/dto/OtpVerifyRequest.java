package com.sanjittech.hms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpVerifyRequest {
    private String email;
    private String otp;
    private String username;
    private String password;
    private String role;
}