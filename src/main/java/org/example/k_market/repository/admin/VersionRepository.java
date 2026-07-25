package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version, String> { // PK 타입 String

    // 최신 등록순 조회
    List<Version> findAllByOrderByCreatedAtDesc();

}
