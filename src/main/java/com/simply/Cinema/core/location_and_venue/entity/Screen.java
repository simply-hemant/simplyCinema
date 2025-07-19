package com.simply.Cinema.core.location_and_venue.entity;

import com.simply.Cinema.core.location_and_venue.Enum.ScreenType;
import com.simply.Cinema.core.location_and_venue.dto.LayoutConfig;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

//    @Column(columnDefinition = "TEXT")
//    private String layoutConfig; // ✅ Stored as JSON in DB

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
