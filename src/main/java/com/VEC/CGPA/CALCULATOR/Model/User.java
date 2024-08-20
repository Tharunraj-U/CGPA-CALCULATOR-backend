package com.VEC.CGPA.CALCULATOR.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String fullName;

    // New fields
    private String department;
    private String section;
    private String rollNumber;
    private String registrationNumber;
    private Double cgpa;
    private  String year;

    @Lob
    @Column(name = "profile_image", columnDefinition="LONGBLOB")
    private byte[] profileImage; // Store image as byte array

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;
}
