package org.example.k_market.service.product;

public record ProductUploadedFiles(
    int thumb1FileId,
    int thumb2FileId,
    int thumb3FileId,
    int detailInfoFileId
) {
}
