package org.example.k_market.controller.admin.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.FileDTO;
import org.example.k_market.service.admin.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FileViewController {

    private final FileService fileService;

    /**
     * 업로드 파일 출력
     */
    @ResponseBody
    @GetMapping("/files/{id}")
    public ResponseEntity<Resource> viewFile(
            @PathVariable("id") Integer id
    ) {
        try {
            FileDTO fileDTO =
                    fileService.findById(id);

            Path filePath =
                    Paths.get(fileDTO.getPath())
                            .toAbsolutePath()
                            .normalize();

            log.info(
                    "파일 출력 요청: id={}, path={}, exists={}",
                    id,
                    filePath,
                    Files.exists(filePath)
            );

            if (!Files.exists(filePath) ||
                    !Files.isRegularFile(filePath)) {

                log.error(
                        "실제 파일이 존재하지 않습니다: id={}, path={}",
                        id,
                        filePath
                );

                return ResponseEntity.notFound().build();
            }

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists() ||
                    !resource.isReadable()) {

                log.error(
                        "파일을 읽을 수 없습니다: id={}, path={}",
                        id,
                        filePath
                );

                return ResponseEntity.notFound().build();
            }

            String contentType =
                    resolveContentType(
                            filePath,
                            fileDTO.getExtension()
                    );

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.parseMediaType(contentType)
                    )
                    .cacheControl(
                            CacheControl.maxAge(
                                    1,
                                    TimeUnit.HOURS
                            )
                    )
                    .body(resource);

        } catch (IllegalArgumentException e) {

            log.error(
                    "파일 DB 정보를 찾을 수 없습니다: id={}",
                    id,
                    e
            );

            return ResponseEntity.notFound().build();

        } catch (MalformedURLException e) {

            log.error(
                    "파일 경로 변환 실패: id={}",
                    id,
                    e
            );

            return ResponseEntity.internalServerError().build();

        } catch (Exception e) {

            log.error(
                    "파일 출력 중 오류 발생: id={}",
                    id,
                    e
            );

            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 확장자에 맞는 Content-Type 반환
     */
    private String resolveContentType(
            Path filePath,
            String extension
    ) {
        try {
            String detectedType =
                    Files.probeContentType(filePath);

            if (detectedType != null &&
                    !detectedType.isBlank()) {

                return detectedType;
            }

        } catch (IOException e) {
            log.warn(
                    "Content-Type 자동 감지 실패: path={}",
                    filePath
            );
        }

        String safeExtension =
                extension == null
                        ? ""
                        : extension.trim().toLowerCase();

        return switch (safeExtension) {
            case "ico" ->
                    "image/x-icon";

            case "png" ->
                    "image/png";

            case "jpg", "jpeg" ->
                    "image/jpeg";

            case "gif" ->
                    "image/gif";

            case "webp" ->
                    "image/webp";

            case "svg" ->
                    "image/svg+xml";

            default ->
                    "application/octet-stream";
        };
    }
}