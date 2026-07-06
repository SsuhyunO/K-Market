package org.example.k_market.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "policy")
public class Policy {
    @Id
    private String policyType;

    @Column(columnDefinition = "TEXT")
    private String content;
}
