package com.simply.Cinema.core.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String firstName;

    private String lastName;

    @Column(unique = true)
    private String phone;

    private LocalDate dateOfBirth;

    private String gender;

    private String profilePictureUrl;

    private Boolean isEmailVerified = false;

    private Boolean isPhoneVerified = false;

    private Boolean isActive = true;

    private String preferredLanguage;

    private Long preferredCityId;

    private Integer loyaltyPoints = 0;

    private String moodPreferences; // Example: "Action, Comedy"

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;
}
