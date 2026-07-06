package org.example.k_market.dto.admin;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminConfigDTO {

    private int id;
    private int mainSliderBannerId;
    private String siteName;
    private String siteSubName;
    private int headerLogoImageId;
    private int footerLogoImageId;
    private int faviconImageId;
    private int logoFiled;
    private String bussName;
    private String ceo;
    private String bussRegNum;
    private String mailOrdBussReg;
    private String defaultAddr;
    private String detailAddr;
    private String csPhone;
    private String csBussHours;
    private String csEmail;
    private String csElectronicDisputePhone;
    private int mainTopBannerId;
    private int prodDetailViewBannerId;
    private int userLoginBannerId;
    private int myPageBannerId;
    private String copyright;
}