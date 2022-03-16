package com.hanghae99.boilerplate.board.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.boilerplate.board.dto.*;
import com.hanghae99.boilerplate.security.model.MemberContext;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface BoardService {
    void createBoard(BoardRequestDto boardRequestDto, MemberContext user);

    List<BoardResponseDto> showAllBoard(Pageable pageable);

    BoardResponseDto showBoard(Long boardId);

    List<BoardResponseDto> showBoardByCategory(String categoryName, Pageable pageable);

    void deleteBoard(Long boardId);

    void recommendBoard(Long boardId, MemberContext user);

    void agreeBoard(Long boardId, MemberContext user);

    void disagreeBoard(Long boardId, MemberContext user);

    void createComment(CommentRequestDto commentRequestDto, MemberContext user);

    List<CommentResponseDto> showComments(Long boardId);

    void deleteComment(Long commentId);

    void recommendComment(Long commentId, MemberContext user);

    void createReply(Long commentId ,ReplyRequestDto replyRequestDto, MemberContext user) throws ExecutionException, InterruptedException, JsonProcessingException;

    List<ReplyResponseDto> showReplies(Long commentId);

    List<BoardResponseDto> getMyBoard(MemberContext user);
    void setMyBoard(Long boardId, MemberContext user);

    List<BoardResponseDto> searchBoard(String content, Pageable pageable);
}
