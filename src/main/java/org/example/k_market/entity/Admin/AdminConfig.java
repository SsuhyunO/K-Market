package org.example.k_market.entity.Admin;


import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "adminConfig")
public class AdminConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int mainSliderBannerId;
    private String siteName;
    private String siteSubName;
    private int headerLogoFiled;
    private int footerLogoFiled;
    private int faviconFiled;
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