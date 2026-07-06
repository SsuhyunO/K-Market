package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.BannerDTO;
import org.example.k_market.entity.admin.Banner;
import org.example.k_market.repository.admin.BannerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class BannerService {

    private final BannerRepository bannerRepository;

    // 배너 등록 및 수정
    public void register(BannerDTO dto) {
        Banner banner = Banner.builder()
                .bannerId(dto.getBannerId())
                .bannerType(dto.getBannerType())
                .fileId(dto.getFileId())
                .name(dto.getName())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .bgColor(dto.getBgColor())
                .link(dto.getLink())
                .enabled(dto.getEnabled())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build();

        bannerRepository.save(banner);
    }

    // 배너 목록 전체 조회
    @Transactional(readOnly = true)
    public List<BannerDTO> findAll() {
        return bannerRepository.findAll().stream()
                .map(banner -> BannerDTO.builder()
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
                        .build())
                .collect(Collectors.toList());
    }

    // 배너 삭제
    public void delete(Integer bannerId) {
        bannerRepository.deleteById(bannerId);
    }
}