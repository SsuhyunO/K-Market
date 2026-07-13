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
@Table(name = "version")
public class Version {

    @Id
    private String id;

    @Column(name = "version", nullable = false, length = 50)
    private String version;      // 예: 0.0.1-SNAPSHOT

    private String writerUid;

    @Column(columnDefinition = "TEXT")
    @Setter                      // 배포 자동등록 후 관리자가 상세 변경내역만 수정할 수 있도록 추가
    private String content;      // 변경내역, 줄바꿈(\n)으로 여러 항목 구분

    @CreationTimestamp
    private LocalDateTime createdAt;
}