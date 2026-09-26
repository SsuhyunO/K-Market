package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.BannerDTO;
import org.example.k_market.entity.admin.Banner;
import org.example.k_market.repository.admin.BannerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BannerService {

    private final BannerRepository bannerRepository;
    private final FileService fileService;

    // 배너 등록
    public void register(BannerDTO dto, MultipartFile bannerFile) {

        validateExposurePeriod(dto);
        if (bannerFile == null || bannerFile.isEmpty()) {
            throw new IllegalArgumentException("배너 이미지를 선택해주세요.");
        }

        int fileId = 0;

        // 배너 이미지 업로드
        if (bannerFile != null && !bannerFile.isEmpty()) {
            var fileDTO = fileService.uploadFile(bannerFile);
            fileId = fileDTO.getId();
        }

        Banner banner = Banner.builder()
                .bannerType(dto.getBannerType())
                .fileId(fileId)
                .name(dto.getName())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .bgColor(dto.getBgColor())
                .link(dto.getLink())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : true)
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build();

        bannerRepository.save(banner);
    }

    // 배너 전체 목록 조회
    @Transactional(readOnly = true)
    public List<BannerDTO> findAll() {

        return bannerRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 배너 타입별 목록 조회
    // mainTop, mainSlider, productDetailView, userLogin, myPage
    @Transactional(readOnly = true)
    public List<BannerDTO> findByBannerType(String bannerType) {

        // bannerType이 없으면 기본값 mainTop
        if (bannerType == null || bannerType.isBlank()) {
            bannerType = "mainTop";
        }

        return bannerRepository.findByBannerTypeOrderByBannerIdDesc(bannerType)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 활성화된 배너 전체 조회
    @Transactional(readOnly = true)
    public List<BannerDTO> findEnabledBanners() {

        return bannerRepository.findActiveBanners(LocalDateTime.now())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 특정 타입의 활성화된 배너 조회
    // 메인화면 출력용
    @Transactional(readOnly = true)
    public List<BannerDTO> findEnabledBannersByType(String bannerType) {

        if (bannerType == null || bannerType.isBlank()) {
            bannerType = "mainTop";
        }

        return bannerRepository.findActiveBannersByType(bannerType, LocalDateTime.now())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 특정 타입의 활성 배너 1개 조회
// userLogin, myPage 등 사용자 화면 출력용
    @Transactional(readOnly = true)
    public BannerDTO findFirstEnabledBannerByType(String bannerType) {

        if (bannerType == null || bannerType.isBlank()) {
            return null;
        }

        return bannerRepository
                .findActiveBannersByType(bannerType, LocalDateTime.now())
                .stream()
                .findFirst()
                .map(this::toDTO)
                .orElse(null);
    }

    // 배너 활성 / 비활성 변경
    public void toggleEnabled(Integer bannerId) {

        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다. bannerId=" + bannerId));

        Boolean current = banner.getEnabled();

        banner.changeEnabled(!Boolean.TRUE.equals(current));
    }

    // 배너 단건 삭제
    public void delete(Integer bannerId) {
        Banner banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 배너를 찾을 수 없습니다."));

        int fileId = banner.getFileId();
        bannerRepository.delete(banner);
        bannerRepository.flush();
        fileService.deleteIfExists(fileId);
    }

    // 배너 선택 삭제
    public void deleteSelected(List<Integer> bannerIds) {

        if (bannerIds == null || bannerIds.isEmpty()) {
            return;
        }

        List<Banner> banners = bannerRepository.findAllById(bannerIds);
        bannerRepository.deleteAll(banners);
        bannerRepository.flush();
        banners.stream()
                .map(Banner::getFileId)
                .distinct()
                .forEach(fileService::deleteIfExists);
    }

    // Entity → DTO 변환
    private BannerDTO toDTO(Banner banner) {

        return BannerDTO.builder()
                .bannerId(banner.getBannerId())
                .bannerType(banner.getBannerType())
                .fileId(banner.getFileId())
                .name(banner.getName())
                .width(banner.getWidth())
                .height(banner.getHeight())
                .bgColor(banner.getBgColor())
                .link(banner.getLink())
                .enabled(banner.getEnabled())
                .startAt(banner.getStartAt())
                .endAt(banner.getEndAt())
                .build();
    }


    // 배너 선택 수정
    public void modify(BannerDTO dto, MultipartFile bannerFile) {
        validateExposurePeriod(dto);

        // 1. 기존 배너 정보 조회
        Banner banner = bannerRepository.findById(dto.getBannerId())
                .orElseThrow(() -> new IllegalArgumentException("수정할 배너를 찾을 수 없습니다."));

        int previousFileId = banner.getFileId();
        int fileId = previousFileId;

        // 2. 새로운 파일이 들어왔다면 기존 파일 처리 및 업로드
        if (bannerFile != null && !bannerFile.isEmpty()) {
            var fileDTO = fileService.uploadFile(bannerFile);
            fileId = fileDTO.getId();
        }

        // 3. 엔티티 상태 업데이트
        banner.changeInfo(
                dto.getName(),
                dto.getWidth(),
                dto.getHeight(),
                dto.getBgColor(),
                dto.getLink(),
                fileId,
                dto.getStartAt(),
                dto.getEndAt()
        );

        if (bannerFile != null && !bannerFile.isEmpty()) {
            bannerRepository.flush();
            fileService.deleteIfExists(previousFileId);
        }

    }

    // 배너 단건 조회 (DTO 변환 포함)
    @Transactional(readOnly = true)
    public BannerDTO findById(Integer bannerId) {
        return bannerRepository.findById(bannerId)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("배너를 찾을 수 없습니다."));
    }

    private void validateExposurePeriod(BannerDTO dto) {
        if (dto.getStartAt() == null || dto.getEndAt() == null) {
            throw new IllegalArgumentException("배너 노출 시작일시와 종료일시를 입력해주세요.");
        }

        if (dto.getStartAt().isAfter(dto.getEndAt())) {
            throw new IllegalArgumentException("배너 종료일시는 시작일시보다 빠를 수 없습니다.");
        }
    }

}
