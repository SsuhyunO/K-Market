package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.FileDTO;
import org.example.k_market.entity.admin.File;
import org.example.k_market.repository.admin.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FileService {

    private final FileRepository fileRepository;

    // 파일 메타데이터 등록
    public void register(FileDTO dto) {
        File file = File.builder()
                .storedName(dto.getStoredName())
                .originalName(dto.getOriginalName())
                .path(dto.getPath())
                .extension(dto.getExtension())
                .fileSize(dto.getFileSize())
                .createdAt(dto.getCreatedAt())
                .build();
        fileRepository.save(file);
    }

    // 파일 단건 조회
    @Transactional(readOnly = true)
    public FileDTO findById(Integer id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일입니다. id=" + id));
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

    // 파일 삭제
    public void delete(Integer id) {
        fileRepository.deleteById(id);
    }
}