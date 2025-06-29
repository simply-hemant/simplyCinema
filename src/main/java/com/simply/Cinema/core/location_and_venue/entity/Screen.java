package com.simply.Cinema.core.location_and_venue.entity;

import com.simply.Cinema.core.location_and_venue.Enum.ScreenType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "theatre_id")
    private Theatre theatre;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private Integer totalSeats;

    @Lob
    private String layoutConfig; // Store seat map JSON or string

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
