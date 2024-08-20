package com.VEC.CGPA.CALCULATOR.Controller;

import com.VEC.CGPA.CALCULATOR.AuthResponse;
import com.VEC.CGPA.CALCULATOR.Configuration.JwtProvider;
import com.VEC.CGPA.CALCULATOR.LoginRequest;
import com.VEC.CGPA.CALCULATOR.Model.Role;
import com.VEC.CGPA.CALCULATOR.Model.User;
import com.VEC.CGPA.CALCULATOR.Repo.UserRepository;
import com.VEC.CGPA.CALCULATOR.Service.CustomerUserDetailsService;
import com.VEC.CGPA.CALCULATOR.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        // Check if the email is already registered
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("Email is already used in another account");
        }

        // Create new user and set its attributes
        User createdUser = new User();
        createdUser.setEmail(user.getEmail());
        createdUser.setFullName(user.getFullName());
        createdUser.setRoles(user.getRoles());
        createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(createdUser);

        // Authenticate the user and generate a JWT token
        Authentication authentication = authenticate(user.getEmail(), user.getPassword());
        String jwt = jwtProvider.generateToken(authentication);


        // Prepare the response with JWT token and success message
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Registration successful");
        try {
            emailService.sendWelcomeEmail(user.getEmail());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest request) {
        // Authenticate the user and generate a JWT token
        Authentication authentication = authenticate(request.getEmail(), request.getPassword());
        String jwt = jwtProvider.generateToken(authentication);

        // Extract user role from the authenticated user
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        // Prepare the response with JWT token and success message
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(jwt);
        authResponse.setMessage("Login successful");
        try {
            authResponse.setRoles(Role.valueOf(role));
        } catch (IllegalArgumentException e) {
            authResponse.setRoles(null); // Handle unexpected roles gracefully
        }
        try {
            emailService.sendLoginEmail(request.getEmail());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        // Load user details using the custom user details service
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username.");
        }
        

        // Check if the provided password matches the stored password
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password.");
        }

        // Return authenticated user with authorities
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
