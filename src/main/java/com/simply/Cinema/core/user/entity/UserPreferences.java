package com.simply.Cinema.core.user.entity;

import com.simply.Cinema.core.user.Enum.GenreType;
import com.simply.Cinema.core.user.Enum.Language;
import com.simply.Cinema.core.user.Enum.SeatPreference;
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
    @JoinColumn(name = "user_id")
    private User user;

    // Genres the user prefers (comma-separated or JSON array)
    @ElementCollection
    @CollectionTable(name = "user_preferred_genres", joinColumns = @JoinColumn(name = "user_preferences_id"))
    @Column(name = "genre")
    private List<GenreType> preferredGenres;

    // Languages the user prefers
    @ElementCollection
    @CollectionTable(name = "user_preferred_languages", joinColumns = @JoinColumn(name = "user_preferences_id"))
    @Column(name = "language")
    private List<Language> preferredLanguages;

    // Notification preferences
    private Boolean notificationEmail;
    private Boolean notificationSms;
    private Boolean notificationPush;

    // Marketing emails consent
    private Boolean marketingEmails;

    // Preferred seating (example: FRONT, MIDDLE, BACK)
    @Enumerated(EnumType.STRING)
    private SeatPreference seatPreference;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
