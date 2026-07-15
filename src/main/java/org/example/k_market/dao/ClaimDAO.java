package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.order.response.ClaimListResponse;

import java.util.List;

@Mapper
public interface ClaimDAO {
    List<ClaimListResponse> selectClaims(@Param("claimType") String claimType,
                                         @Param("sellerUid") String sellerUid,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    int selectClaimCount(@Param("claimType") String claimType,
                         @Param("sellerUid") String sellerUid);

    ClaimListResponse selectClaimByNo(@Param("claimNo") int claimNo,
                                      @Param("sellerUid") String sellerUid);

    int updateClaimStatus(@Param("claimNo") int claimNo,
                          @Param("claimStatus") String claimStatus);

    int updateReshippingExchangeClaimsToCompleted();

    int updateReturnedClaimsToCompleted();
}
