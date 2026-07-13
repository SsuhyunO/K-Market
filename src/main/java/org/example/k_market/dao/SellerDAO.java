package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.k_market.dto.seller.response.SellerStatisticsResponse;
import org.example.k_market.entity.Seller;

import java.util.List;

@Mapper
public interface SellerDAO {

    // 판매자 등록 (INSERT)
    int insertSeller(Seller seller);

    // uid로 판매자 정보 조회
    Seller findByUid(String uid);

    // 사업자등록번호 중복확인
    int countByBizRegNo(String bizRegNo);

    // 판매자 정보 수정
    int updateSeller(Seller seller);

    List<SellerStatisticsResponse> findStatisticsBySellerUids(
        @Param("sellerUids") List<String> sellerUids
    );
}