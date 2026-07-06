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
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bannerId;
    private String bannerType;
    private int fileId;
    private String name;
    private int width;
    private int height;
    private String bgColor;
    private String link;
    private Boolean enabled;

    @CreationTimestamp
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
