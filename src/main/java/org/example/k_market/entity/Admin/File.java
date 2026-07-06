package org.example.k_market.entity.Admin;

import jakarta.persistence.*;
import jdk.jfr.Timespan;
import lombok.*;

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
    private int fileSize;
    private LocalDateTime createdAt;
}
