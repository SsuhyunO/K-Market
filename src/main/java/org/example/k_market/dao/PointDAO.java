package org.example.k_market.dao;

import org.apache.ibatis.annotations.Mapper;
import org.example.k_market.dto.PointDTO;

import java.util.List;

@Mapper
public interface PointDAO {
    void insertPoint(PointDTO point);
    int selectPointBalance(String memberUid);
    List<PointDTO> selectPointHistory(String memberUid);
}
