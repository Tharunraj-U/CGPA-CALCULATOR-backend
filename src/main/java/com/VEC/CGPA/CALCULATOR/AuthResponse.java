package com.VEC.CGPA.CALCULATOR;

import com.VEC.CGPA.CALCULATOR.Model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String jwt;
    private String message;
    private Role roles;
}
