package org.example.k_market.repository.admin;

import org.example.k_market.entity.admin.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruitRepository extends JpaRepository<Recruit, Integer> {
}
