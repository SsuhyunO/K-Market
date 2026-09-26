package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.AdminConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminConfigRepository extends JpaRepository<AdminConfig, Integer> {

    Optional<AdminConfig> findFirstByOrderByIdAsc();
}
