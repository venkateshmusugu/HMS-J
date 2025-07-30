package com.sanjittech.hms.controller;

import com.sanjittech.hms.model.Hospital;
import com.sanjittech.hms.model.HospitalConfig;
import com.sanjittech.hms.service.HospitalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hospital-config")
@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
public class HospitalConfigController {

    @Autowired
    private HospitalConfigService service;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            String url = service.saveLogo(file);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Upload failed: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<HospitalConfig> saveConfig(@RequestBody HospitalConfig config) {
        return ResponseEntity.ok(service.save(config));
    }

    @GetMapping
    public ResponseEntity<HospitalConfig> getConfig() {
        return ResponseEntity.ok(service.get());
    }
}
