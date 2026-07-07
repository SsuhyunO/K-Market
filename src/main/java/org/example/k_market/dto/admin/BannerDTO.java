package org.example.k_market.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerDTO {

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

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    /*
     * =====================================================
     * 화면 출력용 getter
     * banner.html에서 기존에 쓰던 이름들과 맞추기 위한 메서드
     * =====================================================
     */

    // banner.html의 ${banner.no} 대응
    public int getNo() {
        return bannerId;
    }

    // banner.html의 ${banner.size} 대응
    public String getSize() {
        return width + " x " + height;
    }

    // banner.html의 ${banner.color} 대응
    public String getColor() {
        return bgColor;
    }

    // banner.html의 ${banner.url} 대응
    public String getUrl() {
        return link;
    }

    // banner.html의 ${banner.location} 대응
    public String getLocation() {
        return bannerType;
    }

    // banner.html의 ${banner.active} 대응
    public String getActive() {
        return Boolean.TRUE.equals(enabled) ? "ACTIVE" : "INACTIVE";
    }

    // banner.html의 ${banner.imagePath} 대신 사용할 수 있음
    public String getImagePath() {
        if (fileId == 0) {
            return "";
        }

        return "/files/" + fileId;
    }

    // banner.html의 ${banner.createAt} 대응
    public String getCreateAt() {
        return startAt != null ? startAt.toString() : "";
    }

    // banner.html의 ${banner.leaveAt} 대응
    public String getLeaveAt() {
        return endAt != null ? endAt.toString() : "";
    }
}