package org.example.k_market.repository.admin;

import org.example.k_market.entity.Admin.BoardReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoardReplyRepository extends JpaRepository<BoardReply, Integer> {

    Optional<BoardReply> findByBoardNo(Integer boardNo);

    void deleteByBoardNo(Integer boardNo);

}
