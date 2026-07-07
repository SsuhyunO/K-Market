package org.example.k_market.controller.admin.file;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.FileDTO;
import org.example.k_market.service.admin.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Controller
public class FileViewController {

    private final FileService fileService;

    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> viewFile(@PathVariable Integer id) {

        try {
            FileDTO fileDTO = fileService.findById(id);

            Path path = Paths.get(fileDTO.getPath());
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String extension = fileDTO.getExtension();

            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

            if ("png".equalsIgnoreCase(extension)) {
                mediaType = MediaType.IMAGE_PNG;
            } else if ("jpg".equalsIgnoreCase(extension) || "jpeg".equalsIgnoreCase(extension)) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if ("gif".equalsIgnoreCase(extension)) {
                mediaType = MediaType.IMAGE_GIF;
            } else if ("webp".equalsIgnoreCase(extension)) {
                mediaType = MediaType.parseMediaType("image/webp");
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}