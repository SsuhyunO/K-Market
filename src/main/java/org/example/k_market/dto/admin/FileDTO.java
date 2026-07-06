package org.example.k_market.dto.admin;

import lombok.*;
import org.example.k_market.entity.admin.File;
import org.hibernate.boot.model.source.spi.IdentifierSourceNonAggregatedComposite;

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

    public File toEntity(){
        return File.builder()
                .id(this.id)
                .storedName(this.storedName)
                .originalName(this.originalName)
                .path(this.path)
                .extension(this.extension)
                .fileSize((int) this.fileSize)
                .createdAt(this.createdAt)
                .build();
    }
}
