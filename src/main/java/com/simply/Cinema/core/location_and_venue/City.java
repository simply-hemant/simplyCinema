package com.simply.Cinema.core.location_and_venue;

import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

@Entity
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

    private Boolean isActive;

    private LocalDateTime createdAt;

}
