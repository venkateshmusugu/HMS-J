package com.sanjittech.hms.service;

import com.sanjittech.hms.model.HospitalConfig;
import com.sanjittech.hms.repository.HospitalConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class HospitalConfigService {

    @Autowired
    private HospitalConfigRepository repository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";


    public HospitalConfig save(HospitalConfig config) {
        HospitalConfig existing = get();
        existing.setHospitalName(config.getHospitalName() != null ? config.getHospitalName() : existing.getHospitalName());
        existing.setLogoUrl(config.getLogoUrl() != null ? config.getLogoUrl() : existing.getLogoUrl());
        return repository.save(existing);
    }


    public HospitalConfig get() {
        return repository.findById(1L).orElse(new HospitalConfig());
    }

    public String saveLogo(MultipartFile file) throws IOException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        String filename = "hospital_logo_" + System.currentTimeMillis() + ".png";
        File dest = new File(UPLOAD_DIR + filename);
        file.transferTo(dest);

        String logoUrl = "/uploads/" + filename;



        // Save to DB
        HospitalConfig config = get();
        config.setLogoUrl(logoUrl);
        repository.save(config);

        return logoUrl;
    }

}
