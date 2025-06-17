package com.sanjittech.hms.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
@RestController("/api")
public class CSRFController {

    @GetMapping("/csrf")
    public ResponseEntity<?> getCsrfToken(CsrfToken csrfToken) {
        return ResponseEntity.ok().body(Map.of(
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        ));
    }
}
