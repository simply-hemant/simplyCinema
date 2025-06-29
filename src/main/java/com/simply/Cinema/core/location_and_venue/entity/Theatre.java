package com.simply.Cinema.core.location_and_venue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long ownerId; // Can be a User or TheatreOwner reference

    private Long cityId;

    private String address;

    private Double latitude;

    private Double longitude;

    private String phone;

    private String email;

    @ElementCollection
    @CollectionTable(name = "theatre_amenities", joinColumns = @JoinColumn(name = "theatre_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    private Boolean isActive = true;

    private LocalTime openingHour;

    private LocalTime closingHour;

    private Boolean foodBeverageAvailable;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
