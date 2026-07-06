package org.example.k_market.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VersionDTO {
    private String id;
    private String writerUid;
    private String content;
    private LocalDateTime createdAt;
}
