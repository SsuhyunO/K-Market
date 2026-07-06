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
@Table(name = "version")
public class Version {

    @Id
    private String id;
    private String writerUid;
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;
}