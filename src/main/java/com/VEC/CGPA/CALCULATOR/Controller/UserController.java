package com.VEC.CGPA.CALCULATOR.Controller;

import com.VEC.CGPA.CALCULATOR.Model.User;
import com.VEC.CGPA.CALCULATOR.Repo.UserRepository;
import com.VEC.CGPA.CALCULATOR.Service.EmailService;
import com.VEC.CGPA.CALCULATOR.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestParam(required = false) String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/upload-image/{userId}")
    public ResponseEntity<User> uploadProfileImage(@PathVariable Long userId, @RequestParam("image") MultipartFile imageFile) {
        try {
            User updatedUser = userService.updateProfileImage(userId, imageFile);
            return updatedUser != null
                    ? ResponseEntity.ok(updatedUser)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/update-profile")
    public ResponseEntity<User> updateProfile(@RequestBody User user) {
        Optional<User> existingUserOptional = userRepository.findByEmail(user.getEmail());

        if (existingUserOptional.isPresent()) {
            User existingUser = existingUserOptional.get();

            existingUser.setFullName(user.getFullName());
            existingUser.setDepartment(user.getDepartment());
            existingUser.setSection(user.getSection());
            existingUser.setRollNumber(user.getRollNumber());
            existingUser.setRegistrationNumber(user.getRegistrationNumber());
            existingUser.setCgpa(user.getCgpa());
            existingUser.setYear(user.getYear());

            User updatedUser = userRepository.save(existingUser);
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/send-cgpa-pdf")
    public ResponseEntity<String> sendCgpaPdf(@RequestParam String email, @RequestParam double cgpa, @RequestBody byte[] pdfContent) {
        if (email == null || email.isEmpty() || pdfContent == null || pdfContent.length == 0) {
            return ResponseEntity.badRequest().body("Invalid input.");
        }

        try {
            emailService.sendCgpaFile(email, cgpa, pdfContent);
            return ResponseEntity.ok("CGPA PDF sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send CGPA PDF.");
        }
    }
}
