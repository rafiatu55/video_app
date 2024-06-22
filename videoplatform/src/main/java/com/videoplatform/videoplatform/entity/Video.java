package com.videoplatform.videoplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table( name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @Column
    private Long fileSize;

    @Column
    private LocalDateTime dateCreated;

    @PrePersist
    protected  void onClick(){
        this.dateCreated=LocalDateTime.now();
    }


}
