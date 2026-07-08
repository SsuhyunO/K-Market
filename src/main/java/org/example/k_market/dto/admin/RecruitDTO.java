package org.example.k_market.dto.admin;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruitDTO {

    private Integer id;
    private String sellerUid;
    private String department;
    private String experience;
    private String recruitCategory;
    private String title;
    private String status;
    private String content;
    private LocalDateTime recruitStartAt;
    private LocalDateTime recruitEndAt;
    private LocalDateTime createdAt;

}