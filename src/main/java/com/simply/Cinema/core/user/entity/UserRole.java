package com.simply.Cinema.core.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.simply.Cinema.core.user.Enum.UserRoleEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

    private Long theatreId;  // Can be null if role is not linked to a theatre

    private Boolean isActive = true;

    @CreationTimestamp
    private LocalDateTime assignedAt;

    private Long assignedBy; // ID of admin or system that assigned this role

}
