package com.VEC.CGPA.CALCULATOR.Service;

import com.VEC.CGPA.CALCULATOR.Model.User;
import com.VEC.CGPA.CALCULATOR.Repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user=userRepository.findByEmail(username);

        // Corrected condition
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }



        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));

        // Return UserDetails object
        return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), authorities);
    }
}
