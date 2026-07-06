package org.example.k_market.repository.board;

import org.example.k_market.entity.board.Board_reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Board_replyRepository extends JpaRepository<Board_reply, Integer> {
}
