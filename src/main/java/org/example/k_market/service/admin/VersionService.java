package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.VersionDTO;
import org.example.k_market.entity.admin.Version;
import org.example.k_market.repository.admin.VersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VersionService {

    private final VersionRepository versionRepository;

    // 새 사이트 버전 내역 등록
    public void register(VersionDTO dto) {
        Version version = Version.builder()
                .id(dto.getId())
                .writerUid(dto.getWriterUid())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .build();

        versionRepository.save(version);
    }

    // 버전 히스토리 전체 목록 조회
    @Transactional(readOnly = true)
    public List<VersionDTO> findAll() {
        return versionRepository.findAll().stream()
                .map(version -> VersionDTO.builder()
                        .id(version.getId())
                        .writerUid(version.getWriterUid())
                        .content(version.getContent())
                        .createdAt(version.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
