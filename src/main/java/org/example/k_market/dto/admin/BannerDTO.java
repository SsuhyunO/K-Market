package org.example.k_market.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BannerDTO {

    private int bannerId;

    /**
     * 배너 위치 유형
     * mainTop, mainSlider, productDetailView, userLogin, myPage
     */
    private String bannerType;

    /**
     * file 테이블 PK
     */
    private int fileId;

    /**
     * 배너 이름
     */
    private String name;

    /**
     * 배너 너비
     */
    private int width;

    /**
     * 배너 높이
     */
    private int height;

    /**
     * 배경색
     */
    private String bgColor;

    /**
     * 클릭 시 이동할 링크
     */
    private String link;

    /**
     * true: 활성화
     * false: 비활성화
     */
    private Boolean enabled;

    /**
     * 배너 노출 시작일
     */
    private LocalDateTime startAt;

    /**
     * 배너 노출 종료일
     */
    private LocalDateTime endAt;

    /*
     * =========================================================
     * 화면 출력용 Getter
     * 기존 Thymeleaf 변수명과 호환하기 위한 메서드
     * =========================================================
     */

    /**
     * ${banner.no}
     */
    public int getNo() {
        return bannerId;
    }

    /**
     * ${banner.size}
     */
    public String getSize() {
        return width + " x " + height;
    }

    /**
     * ${banner.color}
     */
    public String getColor() {
        return bgColor;
    }

    /**
     * ${banner.url}
     */
    public String getUrl() {
        return link;
    }

    /**
     * ${banner.location}
     */
    public String getLocation() {
        return bannerType;
    }

    /**
     * ${banner.active}
     */
    public String getActive() {
        return Boolean.TRUE.equals(enabled)
                ? "ACTIVE"
                : "INACTIVE";
    }

    /**
     * 배너 이미지 조회 경로
     * 예: /files/15
     */
    public String getImagePath() {
        if (fileId <= 0) {
            return "";
        }

        return "/files/" + fileId;
    }

    /**
     * ${banner.createAt}
     */
    public String getCreateAt() {
        return startAt != null
                ? startAt.toString()
                : "";
    }

    /**
     * ${banner.leaveAt}
     */
    public String getLeaveAt() {
        return endAt != null
                ? endAt.toString()
                : "";
    }
}