package com.VEC.CGPA.CALCULATOR.Service;

import com.VEC.CGPA.CALCULATOR.Model.User;
import com.VEC.CGPA.CALCULATOR.Repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    public User updateProfileImage(Long userId, MultipartFile imageFile) throws IOException, IOException {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfileImage(imageFile.getBytes());
        return userRepository.save(user);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public String encodePassword(String password) {
        return  passwordEncoder.encode(password);
    }
}
