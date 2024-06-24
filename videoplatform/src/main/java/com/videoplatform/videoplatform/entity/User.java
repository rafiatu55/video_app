package com.videoplatform.videoplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
@Table( name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public Role role;

    @Column(nullable = false)
    private boolean verified = false;

    @Column
    @Setter
    @Getter
    private String resetToken;

    @Column
    @Setter
    @Getter
    private LocalDateTime resetTokenExpirationTime;

    @Column
    @Setter
    @Getter
    private String verificationToken;

    @Column
    @Setter
    @Getter
    private LocalDateTime verificationTokenExpirationTime;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt= LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt=LocalDateTime.now();

    @PreUpdate
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate(){
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();

    }

}
