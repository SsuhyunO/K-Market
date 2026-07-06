package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.RecruitDTO;
import org.example.k_market.entity.admin.Recruit;
import org.example.k_market.repository.admin.RecruitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RecruitService {

    private final RecruitRepository recruitRepository;

    // 채용 공고 등록
    public void register(RecruitDTO dto) {
        Recruit recruit = Recruit.builder()
                .sellerUid(dto.getSellerUid())
                .department(dto.getDepartment())
                .experience(dto.getExperience())
                .recruitCategory(dto.getRecruitCategory())
                .title(dto.getTitle())
                .status(dto.getStatus())
                .content(dto.getContent())
                .recruitStartAt(dto.getRecruitStartAt())
                .recruitEndAt(dto.getRecruitEndAt())
                .createdAt(dto.getCreatedAt())
                .build();
        recruitRepository.save(recruit);
    }

    // 채용 공고 전체 조회
    @Transactional(readOnly = true)
    public List<RecruitDTO> findAll() {
        return recruitRepository.findAll().stream()
                .map(recruit -> RecruitDTO.builder()
                        .id(recruit.getId())
                        .sellerUid(recruit.getSellerUid())
                        .department(recruit.getDepartment())
                        .experience(recruit.getExperience())
                        .recruitCategory(recruit.getRecruitCategory())
                        .title(recruit.getTitle())
                        .status(recruit.getStatus())
                        .content(recruit.getContent())
                        .recruitStartAt(recruit.getRecruitStartAt())
                        .recruitEndAt(recruit.getRecruitEndAt())
                        .createdAt(recruit.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 채용 공고 상세 조회
    @Transactional(readOnly = true)
    public RecruitDTO findById(Integer id) {
        Recruit recruit = recruitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채용 공고입니다. id=" + id));
        return RecruitDTO.builder()
                .id(recruit.getId())
                .sellerUid(recruit.getSellerUid())
                .department(recruit.getDepartment())
                .experience(recruit.getExperience())
                .recruitCategory(recruit.getRecruitCategory())
                .title(recruit.getTitle())
                .status(recruit.getStatus())
                .content(recruit.getContent())
                .recruitStartAt(recruit.getRecruitStartAt())
                .recruitEndAt(recruit.getRecruitEndAt())
                .createdAt(recruit.getCreatedAt())
                .build();
    }

    // 채용 공고 삭제
    public void delete(Integer id) {
        recruitRepository.deleteById(id);
    }
}