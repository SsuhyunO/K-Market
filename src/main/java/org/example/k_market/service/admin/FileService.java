package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.FileDTO;
import org.example.k_market.entity.admin.File;
import org.example.k_market.repository.admin.FileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    // 파일 업로드 공통 메서드
    public FileDTO uploadFile(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        try {
            String originalName = multipartFile.getOriginalFilename();

            String extension = "";
            if (originalName != null && originalName.contains(".")) {
                extension = originalName.substring(originalName.lastIndexOf(".") + 1);
            }

            String storedName = UUID.randomUUID() + "_" + originalName;

            Path uploadDir = Paths.get(uploadPath);

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path savePath = uploadDir.resolve(storedName);

            multipartFile.transferTo(savePath.toFile());

            File file = File.builder()
                    .storedName(storedName)
                    .originalName(originalName)
                    .path(savePath.toString())
                    .extension(extension)
                    .fileSize(multipartFile.getSize())
                    .build();

            File savedFile = fileRepository.save(file);

            return toDTO(savedFile);

        } catch (IOException e) {
            log.error("파일 업로드 실패", e);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다.");
        }
    }

    // 파일 단건 조회
    public FileDTO findById(Integer id) {

        File file = fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. id=" + id));

        return toDTO(file);
    }

    // 파일 삭제: 실제 파일 + DB 데이터 삭제
    public void delete(Integer id) {

        File file = fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다. id=" + id));

        try {
            Path filePath = Paths.get(file.getPath());

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }

            fileRepository.deleteById(id);

        } catch (IOException e) {
            log.error("파일 삭제 실패", e);
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.");
        }
    }

    // Entity → DTO 변환 공통 메서드
    private FileDTO toDTO(File file) {
        return FileDTO.builder()
                .id(file.getId())
                .storedName(file.getStoredName())
                .originalName(file.getOriginalName())
                .path(file.getPath())
                .extension(file.getExtension())
                .fileSize(file.getFileSize())
                .createdAt(file.getCreatedAt())
                .build();
    }
}