package org.example.k_market.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.k_market.dto.admin.BoardDTO;
import org.example.k_market.entity.Admin.Board;
import org.example.k_market.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시글 등록
    public void register(BoardDTO dto) {

        Board board = Board.builder()
                .memberUid(dto.getMemberUid())
                .boardType(dto.getBoardType())
                .title(dto.getTitle())
                .content(dto.getContent())
                .fileId(dto.getFileId())
                .build();

        boardRepository.save(board);
    }

    // 게시판 종류별 목록 조회
    public List<BoardDTO> findByBoardType(String boardType) {

        List<Board> boardList = boardRepository.findByBoardTypeOrderByBoardNoDesc(boardType);

        return boardList.stream()
                .map(board -> BoardDTO.builder()
                        .boardNo(board.getBoardNo())
                        .memberUid(board.getMemberUid())
                        .boardType(board.getBoardType())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .fileId(board.getFileId())
                        .createdAt(board.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 게시글 상세보기
    public BoardDTO findById(Integer boardNo) {

        Board board = boardRepository.findById(boardNo)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. boardNo=" + boardNo));

        return BoardDTO.builder()
                .boardNo(board.getBoardNo())
                .memberUid(board.getMemberUid())
                .boardType(board.getBoardType())
                .title(board.getTitle())
                .content(board.getContent())
                .fileId(board.getFileId())
                .createdAt(board.getCreatedAt())
                .build();
    }

    // 게시글 수정
    public void modify(BoardDTO dto) {

        Board board = boardRepository.findById(dto.getBoardNo())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. boardNo=" + dto.getBoardNo()));

        board.updateBoard(
                dto.getBoardType(),
                dto.getTitle(),
                dto.getContent(),
                dto.getFileId()
        );

        boardRepository.save(board);
    }

    // 게시글 삭제
    public void delete(Integer boardNo) {
        boardRepository.deleteById(boardNo);
    }
}
