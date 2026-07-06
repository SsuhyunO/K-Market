package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.BoardReplyDTO;
import org.example.k_market.entity.Admin.BoardReply;
import org.example.k_market.repository.BoardReplyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardReplyService {

    private final BoardReplyRepository boardReplyRepository;

    // 답변 등록
    public void register(BoardReplyDTO dto) {

        BoardReply reply = BoardReply.builder()
                .boardNo(dto.getBoardNo())
                .content(dto.getContent())
                .build();

        boardReplyRepository.save(reply);
    }

    // 게시글 번호로 답변 조회
    public BoardReplyDTO findByBoardNo(Integer boardNo) {

        return boardReplyRepository.findByBoardNo(boardNo)
                .map(reply -> BoardReplyDTO.builder()
                        .replyNo(reply.getReplyNo())
                        .boardNo(reply.getBoardNo())
                        .content(reply.getContent())
                        .createdAt(reply.getCreatedAt())
                        .build())
                .orElse(null);
    }

    // 답변 수정
    public void modify(BoardReplyDTO dto) {

        BoardReply reply = boardReplyRepository.findById(dto.getReplyNo())
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다. replyNo=" + dto.getReplyNo()));

        reply.updateReply(dto.getContent());

        boardReplyRepository.save(reply);
    }

    // 답변 삭제
    @Transactional
    public void deleteByBoardNo(Integer boardNo) {
        boardReplyRepository.deleteByBoardNo(boardNo);
    }
}
