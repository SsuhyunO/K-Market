package org.example.k_market.repository;

import org.example.k_market.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, String> {
    Policy findByPolicyType(String policyType);
}
