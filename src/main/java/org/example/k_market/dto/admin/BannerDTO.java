package org.example.k_market.dto.admin;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerDTO {

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
    private LocalDateTime startAt;
    private LocalDateTime endAt;

}


