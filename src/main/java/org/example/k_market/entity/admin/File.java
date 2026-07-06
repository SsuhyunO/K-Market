package org.example.k_market.entity.admin;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table (name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String storedName;
    private String originalName;
    private String path;
    private String extension;
    private Long fileSize;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
