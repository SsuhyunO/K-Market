package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.k_market.dto.ReviewDTO;

@Mapper
public interface ReviewDAO {
    void insertReview(ReviewDTO dto);
}
