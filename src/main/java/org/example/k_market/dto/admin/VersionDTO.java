package org.example.k_market.dto.admin;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VersionDTO {
    private String id;
    private String version;      // 0.0.1-SNAPSHOT
    private String writerUid;
    private String content;      // 줄바꿈으로 구분된 변경내역 원본
    private LocalDateTime createdAt;

    // content를 줄 단위로 쪼개서 화면 리스트(<li>)에 뿌리기 위한 헬퍼
    public List<String> getContentLines() {
        if (content == null || content.isBlank()) return List.of();
        return Arrays.stream(content.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }
}