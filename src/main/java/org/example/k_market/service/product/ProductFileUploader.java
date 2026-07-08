package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.FileDTO;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.service.admin.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.unit.DataSize;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ProductFileUploader {
    private final FileService fileService;

    @Value("${app.upload.product.thumbnail-max-size:50MB}")
    private DataSize thumbnailMaxSize;

    @Value("${app.upload.product.detail-info-max-size:1MB}")
    private DataSize detailInfoMaxSize;

    public ProductUploadedFiles uploadRequiredFiles(ProductRegisterRequest request) {
        FileDTO thumb1 = uploadRequiredImage(request.getThumb1(), "상품 이미지1", thumbnailMaxSize);
        FileDTO thumb2 = uploadRequiredImage(request.getThumb2(), "상품 이미지2", thumbnailMaxSize);
        FileDTO thumb3 = uploadRequiredImage(request.getThumb3(), "상품 이미지3", thumbnailMaxSize);
        FileDTO detailInfoFile = uploadRequiredImage(request.getDetailInfoFile(), "상품 상세정보 이미지", detailInfoMaxSize);

        return new ProductUploadedFiles(
            thumb1.getId(),
            thumb2.getId(),
            thumb3.getId(),
            detailInfoFile.getId()
        );
    }

    private FileDTO uploadRequiredImage(MultipartFile file, String fieldName, DataSize maxSize) {
        validateImageFile(file, fieldName, maxSize);

        FileDTO uploadedFile = fileService.uploadFile(file);
        if (uploadedFile == null) {
            throw new IllegalArgumentException(fieldName + " 파일은 필수입니다.");
        }
        return uploadedFile;
    }

    private void validateImageFile(MultipartFile file, String fieldName, DataSize maxSize) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " 파일은 필수입니다.");
        }

        if (file.getSize() > maxSize.toBytes()) {
            throw new IllegalArgumentException(fieldName + " 파일은 " + formatDataSize(maxSize) + " 이하만 업로드할 수 있습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(fieldName + " 파일은 이미지 형식만 업로드할 수 있습니다.");
        }
    }

    private String formatDataSize(DataSize dataSize) {
        long bytes = dataSize.toBytes();
        long mb = bytes / (1024 * 1024);
        if (mb > 0 && bytes % (1024 * 1024) == 0) {
            return mb + "MB";
        }
        long kb = bytes / 1024;
        if (kb > 0 && bytes % 1024 == 0) {
            return kb + "KB";
        }
        return bytes + "bytes";
    }
}
