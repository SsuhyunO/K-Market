package org.example.k_market.entity.admin;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recruit")
public class Recruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String sellerUid;
    private String department;
    private String experience;
    private String recruitCategory;
    private String title;
    private String status;
    private String content;

    @CreationTimestamp
    private LocalDateTime recruitStartAt;
    private LocalDateTime recruitEndAt;
    private LocalDateTime createdAt;

    // 채용 공고 수정
    public void changeInfo(String sellerUid,
                           String department,
                           String experience,
                           String recruitCategory,
                           String title,
                           String status,
                           String content,
                           LocalDateTime recruitStartAt,
                           LocalDateTime recruitEndAt) {

        this.sellerUid = sellerUid;
        this.department = department;
        this.experience = experience;
        this.recruitCategory = recruitCategory;
        this.title = title;
        this.status = status;
        this.content = content;
        this.recruitStartAt = recruitStartAt;
        this.recruitEndAt = recruitEndAt;
    }

}