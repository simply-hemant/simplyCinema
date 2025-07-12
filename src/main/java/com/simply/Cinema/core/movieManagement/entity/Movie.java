package com.simply.Cinema.core.movieManagement.entity;

import com.simply.Cinema.core.movieManagement.eunm.MovieRating;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer durationMinutes;

    private LocalDate releaseDate;

    @Enumerated(EnumType.STRING)
    private MovieRating rating;

    private String trailerUrl;
    private String posterUrl;
    private String bannerUrl;

    private boolean isActive = true;

    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "cast_member")
    private List<String> cast;

    @ElementCollection
    @CollectionTable(name = "movie_crew", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "crew_member")
    private List<String> crew;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // Relationships
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieGenre> genres;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieLanguage> languages;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieReview> reviews;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

}
