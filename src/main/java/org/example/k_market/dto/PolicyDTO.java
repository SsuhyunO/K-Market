package org.example.k_market.dto;

import lombok.*;
import org.example.k_market.entity.Policy;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PolicyDTO {
    private String policyType;
    private String content;

    public Policy toEntity(){
        return Policy.builder()
                .policyType(policyType)
                .content(content)
                .build();
    }
}
