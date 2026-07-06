package org.example.k_market.repository.board;

import org.example.k_market.entity.board.BoardReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardReplyRepository extends JpaRepository<BoardReply, Integer> {

    // 특정 게시글 번호에 달린 답변 조회
    Optional<BoardReply> findByBoardNo(Integer boardNo);

    // 특정 게시글 번호에 달린 답변 삭제
    void deleteByBoardNo(Integer boardNo);
}