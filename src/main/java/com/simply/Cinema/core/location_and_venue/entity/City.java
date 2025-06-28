package com.simply.Cinema.core.location_and_venue.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String country;

    @Column(length = 50)
    private String timezone;

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
