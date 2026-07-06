package org.example.k_market.dto.admin;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {

    private int id;
    private String storedName;
    private String originalName;
    private String path;
    private String extension;
    private int fileSize;
    private LocalDateTime createdAt;
}
