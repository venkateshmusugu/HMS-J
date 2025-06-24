package com.sanjittech.hms.controller;

import com.sanjittech.hms.dto.SurgeryLogDto;
import com.sanjittech.hms.model.Surgery;
import com.sanjittech.hms.service.SurgeryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/surgeries")
@CrossOrigin("http://localhost:3002")
public class SurgeryController {

    @Autowired
    private SurgeryService surgeryService;

    @PostMapping("/book/{patientId}")
    public ResponseEntity<?> book(@PathVariable Long patientId, @RequestBody SurgeryLogDto dto) {
        surgeryService.bookSurgery(patientId, dto);
        return ResponseEntity.ok("Surgery booked");
    }

    @PutMapping("/{id}")
    public ResponseEntity<Surgery> update(@PathVariable Long id, @RequestBody SurgeryLogDto dto) {
        return ResponseEntity.ok(surgeryService.updateSurgery(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        surgeryService.deleteSurgery(id);
        return ResponseEntity.ok("Surgery deleted");
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<SurgeryLogDto>> getByDate(@RequestParam String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        List<Surgery> surgeries = surgeryService.getSurgeriesByDate(parsedDate);
        List<SurgeryLogDto> dtoList = surgeries.stream().map(SurgeryLogDto::new).toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Surgery> getById(@PathVariable Long id) {
        return ResponseEntity.ok(surgeryService.getSurgeryById(id));
    }
}
