package org.example.k_market.service;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.PolicyArticleDTO;
import org.example.k_market.dto.PolicyDTO;
import org.example.k_market.entity.Policy;
import org.example.k_market.repository.PolicyRepository;
import org.example.k_market.util.PolicyParser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    /**
     * 1) policyType에 해당하는 약관 전체 content 반환
     * - 수정 페이지 textarea용
     * - 원문 전체 필요할 때 사용
     */
    public String getPolicyContent(String policyType) {
        Policy policy = policyRepository.findByPolicyType(policyType);

        if (policy == null) {
            return "";
        }

        return policy.getContent();
    }

    /**
     * 2) policyType에 해당하는 약관을 조 단위로 split해서 반환
     * - 사용자/관리자 보기 페이지용
     */
    public List<PolicyArticleDTO> getPolicyArticles(String policyType) {
        Policy policy = policyRepository.findByPolicyType(policyType);

        if (policy == null || policy.getContent() == null) {
            return List.of();
        }

        return PolicyParser.splitArticles(policy.getContent());
    }

    public void updatePolicy(PolicyDTO policyDTO) {
        policyRepository.save(policyDTO.toEntity());
    }
}
