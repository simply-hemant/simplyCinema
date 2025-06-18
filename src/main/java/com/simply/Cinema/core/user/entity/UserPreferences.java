package com.simply.Cinema.core.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Foreign key to User entity
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Genres the user prefers (comma-separated or JSON array)
    @ElementCollection
    @CollectionTable(name = "user_preferred_genres", joinColumns = @JoinColumn(name = "user_preferences_id"))
    @Column(name = "genre")
    private List<String> preferredGenres;

    // Languages the user prefers
    @ElementCollection
    @CollectionTable(name = "user_preferred_languages", joinColumns = @JoinColumn(name = "user_preferences_id"))
    @Column(name = "language")
    private List<String> preferredLanguages;

    // Notification preferences
    private Boolean notificationEmail;
    private Boolean notificationSms;
    private Boolean notificationPush;

    // Marketing emails consent
    private Boolean marketingEmails;

    // Preferred seating (example: FRONT, MIDDLE, BACK)
    private String seatPreference;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
