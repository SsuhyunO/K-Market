package org.example.k_market.repository.board;

import org.example.k_market.entity.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    // 게시판 종류별 조회
    List<Board> findByBoardType(String boardType);

    // 게시판 종류별 최신순 조회
    List<Board> findByBoardTypeOrderByBoardNoDesc(String boardType);

    // 작성자별 게시글 조회
    List<Board> findByMemberUid(String memberUid);
}