package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.AdminConfigDTO;
import org.example.k_market.entity.admin.AdminConfig;
import org.example.k_market.repository.admin.AdminConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminConfigService {

    private final AdminConfigRepository adminConfigRepository;
    private final FileService fileService;

    // 환경설정 단건 조회
    public AdminConfigDTO findById(Integer id) {

        AdminConfig config = adminConfigRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("환경설정을 찾을 수 없습니다. id=" + id));

        return toDTO(config);
    }

    // 사이트 제목 / 부제 수정
    @Transactional
    public void modifySiteSettings(AdminConfigDTO dto) {

        AdminConfig config = adminConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("환경설정을 찾을 수 없습니다."));

        config.updateSiteSettings(
                dto.getSiteName(),
                dto.getSiteSubName()
        );
    }

    // 로고 수정
    @Transactional
    public void modifySiteLogo(MultipartFile headerLogo,
                               MultipartFile footerLogo,
                               MultipartFile favicon) {

        AdminConfig config = adminConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("환경설정을 찾을 수 없습니다."));

        int headerLogoFiled = config.getHeaderLogoFiled();
        int footerLogoFiled = config.getFooterLogoFiled();
        int faviconFiled = config.getFaviconFiled();
        int logoFiled = config.getLogoFiled();

        // 헤더 로고 업로드
        if (headerLogo != null && !headerLogo.isEmpty()) {
            var headerLogoFile = fileService.uploadFile(headerLogo);
            headerLogoFiled = headerLogoFile.getId();
        }

        // 푸터 로고 업로드
        if (footerLogo != null && !footerLogo.isEmpty()) {
            var footerLogoFile = fileService.uploadFile(footerLogo);
            footerLogoFiled = footerLogoFile.getId();
        }

        // 파비콘 업로드
        if (favicon != null && !favicon.isEmpty()) {
            var faviconFile = fileService.uploadFile(favicon);
            faviconFiled = faviconFile.getId();
        }

        config.updateSiteLogo(
                headerLogoFiled,
                footerLogoFiled,
                faviconFiled,
                logoFiled
        );
    }

    // 기업 정보 수정
    @Transactional
    public void modifyCorporateInfo(AdminConfigDTO dto) {

        AdminConfig config = adminConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("환경설정을 찾을 수 없습니다."));

        config.updateCorporateInfo(
                dto.getBussName(),
                dto.getCeo(),
                dto.getBussRegNum(),
                dto.getMailOrdBussReg(),
                dto.getDefaultAddr(),
                dto.getDetailAddr()
        );
    }

    // 고객센터 정보 수정
    @Transactional
    public void modifyCustomerSupportInfo(AdminConfigDTO dto) {

        AdminConfig config = adminConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("환경설정을 찾을 수 없습니다."));

        config.updateCustomerSupportInfo(
                dto.getCsPhone(),
                dto.getCsBussHours(),
                dto.getCsEmail(),
                dto.getCsElectronicDisputePhone()
        );
    }

    // 카피라이트 수정
    @Transactional
    public void modifyCopyright(AdminConfigDTO dto) {

        AdminConfig config = adminConfigRepository.findById(1)
                .orElseThrow(() -> new IllegalArgumentException("환경설정을 찾을 수 없습니다."));

        config.updateCopyright(dto.getCopyright());
    }

    // Entity → DTO 변환
    private AdminConfigDTO toDTO(AdminConfig config) {

        return AdminConfigDTO.builder()
                .id(config.getId())
                .mainSliderBannerId(config.getMainSliderBannerId())
                .siteName(config.getSiteName())
                .siteSubName(config.getSiteSubName())
                .headerLogoFiled(config.getHeaderLogoFiled())
                .footerLogoFiled(config.getFooterLogoFiled())
                .faviconFiled(config.getFaviconFiled())
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