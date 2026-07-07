package org.example.k_market.entity.admin;


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

    public void updateSiteSettings(String siteName, String siteSubName) {
        this.siteName = siteName;
        this.siteSubName = siteSubName;
    }

    public void updateSiteLogo(int headerLogoFiled, int footerLogoFiled, int faviconFiled, int logoFiled) {
        this.headerLogoFiled = headerLogoFiled;
        this.footerLogoFiled = footerLogoFiled;
        this.faviconFiled = faviconFiled;
        this.logoFiled = logoFiled;
    }

    public void updateCorporateInfo(String bussName,
                                    String ceo,
                                    String bussRegNum,
                                    String mailOrdBussReg,
                                    String defaultAddr,
                                    String detailAddr) {
        this.bussName = bussName;
        this.ceo = ceo;
        this.bussRegNum = bussRegNum;
        this.mailOrdBussReg = mailOrdBussReg;
        this.defaultAddr = defaultAddr;
        this.detailAddr = detailAddr;
    }

    public void updateCustomerSupportInfo(String csPhone,
                                          String csBussHours,
                                          String csEmail,
                                          String csElectronicDisputePhone) {
        this.csPhone = csPhone;
        this.csBussHours = csBussHours;
        this.csEmail = csEmail;
        this.csElectronicDisputePhone = csElectronicDisputePhone;
    }

    public void updateCopyright(String copyright) {
        this.copyright = copyright;
    }


}