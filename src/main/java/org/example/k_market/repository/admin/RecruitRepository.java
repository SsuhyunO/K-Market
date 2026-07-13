package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitRepository
        extends JpaRepository<Recruit, Integer> {

    // 전체 목록
    List<Recruit> findAllByOrderByIdDesc();

    // 전체 목록 페이지네이션
    Page<Recruit> findAllByOrderByIdDesc(Pageable pageable);

    // 부서 검색
    List<Recruit> findByDepartmentContainingIgnoreCaseOrderByIdDesc(
            String department
    );

    Page<Recruit> findByDepartmentContainingIgnoreCase(
            String department,
            Pageable pageable
    );

    // 경력 검색
    List<Recruit> findByExperienceContainingIgnoreCaseOrderByIdDesc(
            String experience
    );

    Page<Recruit> findByExperienceContainingIgnoreCase(
            String experience,
            Pageable pageable
    );

    // 채용 형태 검색
    List<Recruit> findByRecruitCategoryContainingIgnoreCaseOrderByIdDesc(
            String recruitCategory
    );

    Page<Recruit> findByRecruitCategoryContainingIgnoreCase(
            String recruitCategory,
            Pageable pageable
    );

    // 제목 검색
    List<Recruit> findByTitleContainingIgnoreCaseOrderByIdDesc(
            String title
    );

    Page<Recruit> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );

    /*
     * 번호 검색용
     * 기본 findById(Integer)는 Optional을 반환하므로
     * Pageable 버전을 별도로 선언
     */
    Page<Recruit> findById(Integer id, Pageable pageable);
}