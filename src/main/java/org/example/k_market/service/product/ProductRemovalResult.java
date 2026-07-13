package org.example.k_market.service.product;

public record ProductRemovalResult(
    int hardDeletedCount,
    int stoppedCount,
    String message
) {
    public int totalProcessedCount() {
        return hardDeletedCount + stoppedCount;
    }
}
