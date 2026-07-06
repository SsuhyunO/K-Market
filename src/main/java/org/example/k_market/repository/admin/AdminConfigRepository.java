package org.example.k_market.repository.admin;

import org.example.k_market.entity.Admin.AdminConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminConfigRepository extends JpaRepository<AdminConfig, Integer> {

}