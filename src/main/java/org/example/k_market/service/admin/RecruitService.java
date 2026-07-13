package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.RecruitDTO;
import org.example.k_market.entity.admin.Recruit;
import org.example.k_market.repository.admin.RecruitRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                .createdAt(
                        dto.getCreatedAt() != null
                                ? dto.getCreatedAt()
                                : LocalDateTime.now()
                )
                .build();

        recruitRepository.save(recruit);
    }

    /*
     * 기존 전체 조회
     * 다른 화면에서 전체 목록이 필요할 수 있으므로 유지
     */
    @Transactional(readOnly = true)
    public List<RecruitDTO> findAll() {

        return recruitRepository.findAllByOrderByIdDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * 관리자 채용 목록 페이지네이션 조회
     */
    @Transactional(readOnly = true)
    public Page<RecruitDTO> findAll(Pageable pageable) {

        return recruitRepository.findAllByOrderByIdDesc(pageable)
                .map(this::toDTO);
    }

    /*
     * 기존 검색
     * 페이지네이션을 사용하지 않는 화면을 위해 유지
     */
    @Transactional(readOnly = true)
    public List<RecruitDTO> findRecruits(
            String searchType,
            String keyword
    ) {

        String safeSearchType =
                searchType == null ? "" : searchType.trim();

        String safeKeyword =
                keyword == null ? "" : keyword.trim();

        if (safeKeyword.isBlank()) {
            return findAll();
        }

        List<Recruit> recruitList;

        switch (safeSearchType) {

            case "id":
                try {
                    int id = Integer.parseInt(safeKeyword);

                    recruitList = recruitRepository.findById(id)
                            .map(List::of)
                            .orElseGet(List::of);

                } catch (NumberFormatException e) {
                    recruitList = List.of();
                }
                break;

            case "department":
                recruitList =
                        recruitRepository
                                .findByDepartmentContainingIgnoreCaseOrderByIdDesc(
                                        safeKeyword
                                );
                break;

            case "experience":
                recruitList =
                        recruitRepository
                                .findByExperienceContainingIgnoreCaseOrderByIdDesc(
                                        safeKeyword
                                );
                break;

            case "recruitCategory":
                recruitList =
                        recruitRepository
                                .findByRecruitCategoryContainingIgnoreCaseOrderByIdDesc(
                                        safeKeyword
                                );
                break;

            case "title":
                recruitList =
                        recruitRepository
                                .findByTitleContainingIgnoreCaseOrderByIdDesc(
                                        safeKeyword
                                );
                break;

            default:
                return findAll();
        }

        return recruitList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /*
     * 검색 + 페이지네이션
     */
    @Transactional(readOnly = true)
    public Page<RecruitDTO> findRecruits(
            String searchType,
            String keyword,
            Pageable pageable
    ) {

        String safeSearchType =
                searchType == null ? "" : searchType.trim();

        String safeKeyword =
                keyword == null ? "" : keyword.trim();

        if (safeKeyword.isBlank()) {
            return findAll(pageable);
        }

        Page<Recruit> recruitPage;

        switch (safeSearchType) {

            case "id":
                try {
                    int id = Integer.parseInt(safeKeyword);

                    recruitPage =
                            recruitRepository.findById(id, pageable);

                } catch (NumberFormatException e) {
                    recruitPage = Page.empty(pageable);
                }
                break;

            case "department":
                recruitPage =
                        recruitRepository
                                .findByDepartmentContainingIgnoreCase(
                                        safeKeyword,
                                        pageable
                                );
                break;

            case "experience":
                recruitPage =
                        recruitRepository
                                .findByExperienceContainingIgnoreCase(
                                        safeKeyword,
                                        pageable
                                );
                break;

            case "recruitCategory":
                recruitPage =
                        recruitRepository
                                .findByRecruitCategoryContainingIgnoreCase(
                                        safeKeyword,
                                        pageable
                                );
                break;

            case "title":
                recruitPage =
                        recruitRepository
                                .findByTitleContainingIgnoreCase(
                                        safeKeyword,
                                        pageable
                                );
                break;

            default:
                return findAll(pageable);
        }

        return recruitPage.map(this::toDTO);
    }

    // 채용 공고 상세 조회
    @Transactional(readOnly = true)
    public RecruitDTO findById(Integer id) {

        Recruit recruit = recruitRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 채용 공고입니다. id=" + id
                        )
                );

        return toDTO(recruit);
    }

    // 채용 공고 단건 삭제
    public void delete(Integer id) {

        if (!recruitRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "삭제할 채용 공고가 존재하지 않습니다. id=" + id
            );
        }

        recruitRepository.deleteById(id);
    }

    // 채용 공고 선택 삭제
    public void deleteSelected(List<Integer> recruitNo) {

        if (recruitNo == null || recruitNo.isEmpty()) {
            return;
        }

        recruitRepository.deleteAllById(recruitNo);
    }

    // 채용 공고 수정
    public void modify(RecruitDTO dto) {

        Recruit recruit = recruitRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "수정할 채용 공고를 찾을 수 없습니다. id="
                                        + dto.getId()
                        )
                );

        recruit.changeInfo(
                dto.getSellerUid(),
                dto.getDepartment(),
                dto.getExperience(),
                dto.getRecruitCategory(),
                dto.getTitle(),
                dto.getStatus(),
                dto.getContent(),
                dto.getRecruitStartAt(),
                dto.getRecruitEndAt()
        );
    }

    // Entity -> DTO 변환
    private RecruitDTO toDTO(Recruit recruit) {

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
}