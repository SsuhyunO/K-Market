package org.example.k_market.repository.admin;

import org.example.k_market.entity.Admin.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    List<Board> findByBoardType(String boardType);

    List<Board> findByBoardTypeOrderByBoardNoDesc(String boardType);

    List<Board> findByMemberUid(String memberUid);
}