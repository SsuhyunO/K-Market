package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.AdminConfigDTO;
import org.example.k_market.entity.admin.AdminConfig;
import org.example.k_market.repository.admin.AdminConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class AdminConfigService {

    private final AdminConfigRepository adminConfigRepository;

    // 환경설정 저장 및 수정 (최초 등록 혹은 갱신)
    public void saveConfig(AdminConfigDTO dto) {
        AdminConfig config = AdminConfig.builder()
                .id(dto.getId())
                .mainSliderBannerId(dto.getMainSliderBannerId())
                .siteName(dto.getSiteName())
                .siteSubName(dto.getSiteSubName())
                .headerLogoImageId(dto.getHeaderLogoImageId())
                .footerLogoImageId(dto.getFooterLogoImageId())
                .faviconImageId(dto.getFaviconImageId())
                .logoFiled(dto.getLogoFiled())
                .bussName(dto.getBussName())
                .ceo(dto.getCeo())
                .bussRegNum(dto.getBussRegNum())
                .mailOrdBussReg(dto.getMailOrdBussReg())
                .defaultAddr(dto.getDefaultAddr())
                .detailAddr(dto.getDetailAddr())
                .csPhone(dto.getCsPhone())
                .csBussHours(dto.getCsBussHours())
                .csEmail(dto.getCsEmail())
                .csElectronicDisputePhone(dto.getCsElectronicDisputePhone())
                .mainTopBannerId(dto.getMainTopBannerId())
                .prodDetailViewBannerId(dto.getProdDetailViewBannerId())
                .userLoginBannerId(dto.getUserLoginBannerId())
                .myPageBannerId(dto.getMyPageBannerId())
                .copyright(dto.getCopyright())
                .build();

        adminConfigRepository.save(config);
    }

    // 환경설정 상세 조회
    @Transactional(readOnly = true) // 단순 조회를 위한 성능 최적화
    public AdminConfigDTO getConfig(Integer id) {
        AdminConfig config = adminConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("환경설정 정보가 존재하지 않습니다. id=" + id));


        return AdminConfigDTO.builder()
                .id(config.getId())
                .mainSliderBannerId(config.getMainSliderBannerId())
                .siteName(config.getSiteName())
                .siteSubName(config.getSiteSubName())
                .headerLogoImageId(config.getHeaderLogoImageId())
                .footerLogoImageId(config.getFooterLogoImageId())
                .faviconImageId(config.getFaviconImageId())
                .logoFiled(config.getLogoFiled())
                .bussName(config.getBussName())
                .ceo(config.getCeo())
                .bussRegNum(config.getBussRegNum())
                .mailOrdBussReg(config.getMailOrdBussReg())
                .defaultAddr(config.getDefaultAddr())
                .detailAddr(config.getDetailAddr())
                .csPhone(config.getCsPhone())
                .csBussHours(config.getCsBussHours())
                .csEmail(config.getCsEmail())
                .csElectronicDisputePhone(config.getCsElectronicDisputePhone())
                .mainTopBannerId(config.getMainTopBannerId())
                .prodDetailViewBannerId(config.getProdDetailViewBannerId())
                .userLoginBannerId(config.getUserLoginBannerId())
                .myPageBannerId(config.getMyPageBannerId())
                .copyright(config.getCopyright())
                .build();
    }
}