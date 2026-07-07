package org.example.k_market.service.product;

import lombok.RequiredArgsConstructor;
import org.example.k_market.dto.admin.FileDTO;
import org.example.k_market.dto.product.request.ProductRegisterRequest;
import org.example.k_market.service.admin.FileService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class ProductFileUploader {
    private final FileService fileService;

    public ProductUploadedFiles uploadRequiredFiles(ProductRegisterRequest request) {
        FileDTO thumb1 = uploadRequiredImage(request.getThumb1(), "상품 이미지1");
        FileDTO thumb2 = uploadRequiredImage(request.getThumb2(), "상품 이미지2");
        FileDTO thumb3 = uploadRequiredImage(request.getThumb3(), "상품 이미지3");
        FileDTO detailInfoFile = uploadRequiredImage(request.getDetailInfoFile(), "상품 상세정보 이미지");

        return new ProductUploadedFiles(
            thumb1.getId(),
            thumb2.getId(),
            thumb3.getId(),
            detailInfoFile.getId()
        );
    }

    private FileDTO uploadRequiredImage(MultipartFile file, String fieldName) {
        validateImageFile(file, fieldName);

        FileDTO uploadedFile = fileService.uploadFile(file);
        if (uploadedFile == null) {
            throw new IllegalArgumentException(fieldName + " 파일은 필수입니다.");
        }
        return uploadedFile;
    }

    private void validateImageFile(MultipartFile file, String fieldName) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " 파일은 필수입니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(fieldName + " 파일은 이미지 형식만 업로드할 수 있습니다.");
        }
    }
}
