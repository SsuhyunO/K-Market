package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, String> { // PK 타입 String
}
