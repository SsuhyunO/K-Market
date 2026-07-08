package org.example.k_market.entity.admin;

import jakarta.persistence.*;
import lombok.*;

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

    // mainTop, mainSlider, productDetailView, userLogin, myPage
    private String bannerType;

    // file 테이블 id
    private int fileId;

    private String name;

    private int width;

    private int height;

    private String bgColor;

    private String link;

    // true: 활성, false: 비활성
    private Boolean enabled;

    // 배너 노출 시작일시
    private LocalDateTime startAt;

    // 배너 노출 종료일시
    private LocalDateTime endAt;

    // 배너 전체 정보 수정
    public void updateBanner(String bannerType, int fileId, String name, int width, int height,
                             String bgColor, String link, Boolean enabled,
                             LocalDateTime startAt, LocalDateTime endAt) {
        this.bannerType = bannerType;
        this.fileId = fileId;
        this.name = name;
        this.width = width;
        this.height = height;
        this.bgColor = bgColor;
        this.link = link;
        this.enabled = enabled;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // 배너 선택 수정
    public void changeInfo(String name,
                           int width,
                           int height,
                           String bgColor,
                           String link,
                           int fileId,
                           LocalDateTime startAt,
                           LocalDateTime endAt) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.bgColor = bgColor;
        this.link = link;
        this.fileId = fileId;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // 활성 / 비활성 변경
    public void changeEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}