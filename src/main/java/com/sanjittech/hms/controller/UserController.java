//package com.sanjittech.hms.controller;
//
//import com.sanjittech.hms.model.User;
//import com.sanjittech.hms.repository.UserRepository;
//import com.sanjittech.hms.service.UserService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.csrf.CsrfToken;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/users")
//@CrossOrigin(origins = "http://localhost:3002", allowCredentials = "true")
//
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @GetMapping("/api/users/me")
//    public ResponseEntity<User> currentUser(Authentication auth) {
//        User user = userService.findByUsername(auth.getName());
//        return ResponseEntity.ok(new UserDto(user.getUsername()));
//    }
//
//
//
//
//
//}
