package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.VersionDTO;
import org.example.k_market.entity.admin.Version;
import org.example.k_market.repository.admin.VersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class VersionService {

    private final VersionRepository versionRepository;

    // 새 사이트 버전 내역 등록 (관리자 수동 등록용)
    public void register(VersionDTO dto) {
        String id = "V" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        Version version = Version.builder()
                .id(id)
                .version(dto.getVersion())
                .writerUid(dto.getWriterUid())
                .content(dto.getContent())
                .build(); // createdAt은 @CreationTimestamp 로 자동 세팅

        versionRepository.save(version);
    }

    // 선택 삭제
    public void deleteAll(List<String> ids) {
        versionRepository.deleteAllById(ids);
    }

    // 버전 히스토리 전체 목록 조회 (최신순)
    @Transactional(readOnly = true)
    public List<VersionDTO> findAll() {
        return versionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(version -> VersionDTO.builder()
                        .id(version.getId())
                        .version(version.getVersion())
                        .writerUid(version.getWriterUid())
                        .content(version.getContent())
                        .createdAt(version.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 등록된 버전 내역 수정 (버전명 + 변경내역) - 수정 기능 추가
    public void update(VersionDTO dto) {
        Version version = versionRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 버전 정보입니다. id=" + dto.getId()));
        version.setVersion(dto.getVersion());
        version.setContent(dto.getContent());
        // JPA dirty checking 으로 트랜잭션 종료 시 자동 UPDATE
    }
}