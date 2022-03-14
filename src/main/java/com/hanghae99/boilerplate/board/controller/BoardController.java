package com.hanghae99.boilerplate.board.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.boilerplate.board.dto.*;
import com.hanghae99.boilerplate.board.service.BoardService;
import com.hanghae99.boilerplate.config.BaseResponse;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;


@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@RequestMapping("/auth")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/api/board")
    public void createBoard(@RequestBody BoardRequestDto boardRequestDto, @AuthenticationPrincipal MemberContext user){
        boardService.createBoard(boardRequestDto, user);
    }

    @GetMapping("/api/board")
    public List<BoardResponseDto> getBoards(Pageable pageable){
        return boardService.showAllBoard(pageable);
    }

    @GetMapping("/api/board/{boardId}")
    public BoardResponseDto getBoard(@PathVariable Long boardId){
        return boardService.showBoard(boardId);
    }

    @GetMapping("/api/board/category/{categoryName}")
    public List<BoardResponseDto> getBoardByCategory(@PathVariable String categoryName, Pageable pageable){
        return boardService.showBoardByCategory(categoryName, pageable);
    }

    @GetMapping("/api/board/agree/{boardId}") //찬성, 취소
    public BaseResponse agreeBoard(@PathVariable Long boardId, @AuthenticationPrincipal MemberContext user){
        boardService.agreeBoard(boardId, user);
        return new BaseResponse("ok");
    }

    @GetMapping("/api/board/disagree/{boardId}")
    public BaseResponse disagreeBoard(@PathVariable Long boardId, @AuthenticationPrincipal MemberContext user){
        boardService.disagreeBoard(boardId, user);
        return new BaseResponse("ok");
    }

    @GetMapping("/api/board/recommend/{boardId}")
    public BaseResponse recommendBoard(@PathVariable Long boardId, @AuthenticationPrincipal MemberContext user){
        boardService.recommendBoard(boardId, user);
        return new BaseResponse("ok");
    }

    @DeleteMapping("/api/board/{boardId}")
    public BaseResponse deleteBoard(@PathVariable Long boardId){
        boardService.deleteBoard(boardId);
        return new BaseResponse("ok");
    }

    @PostMapping("/api/comment")
    public BaseResponse createComment(@RequestBody CommentRequestDto commentRequestDto, @AuthenticationPrincipal MemberContext user){
        boardService.createComment(commentRequestDto, user);
        return new BaseResponse("ok");
    }

    @GetMapping("/api/comment/{boardId}")
    public List<CommentResponseDto> showComments(@PathVariable Long boardId){
        return boardService.showComments(boardId);
    }
    @DeleteMapping("/api/comment/{commentId}")
    public BaseResponse deleteComment(@PathVariable Long commentId){
        boardService.deleteComment(commentId);
        return new BaseResponse("ok");

    }

    @GetMapping("/api/comment/recommend/{commentId}")
    public BaseResponse recommendComment(@PathVariable Long commentId,@AuthenticationPrincipal MemberContext user){
        boardService.recommendComment(commentId, user);
        return new BaseResponse("ok");

    }

    //대댓글 달기
    @PostMapping("/api/comment/{commentId}/reply")
    public BaseResponse createReply(@PathVariable Long commentId ,@RequestBody ReplyRequestDto replyRequestDto, @AuthenticationPrincipal MemberContext user) throws ExecutionException, InterruptedException, JsonProcessingException {

        boardService.createReply(commentId, replyRequestDto, user);
        return new BaseResponse("ok");
    }
    //대댓글 보기

    @GetMapping("/api/comment/{commentId}/reply")
    public List<ReplyResponseDto> showReply(@PathVariable Long commentId){
        return boardService.showReplies(commentId);
    }

    @PostMapping("/api/my-board/{boardId}")
    public BaseResponse createMyboard(@PathVariable Long boardId, @AuthenticationPrincipal MemberContext user){
        boardService.setMyBoard(boardId, user);
        return new BaseResponse("ok");
    }

    @GetMapping("/api/my-board")
    public List<BoardResponseDto> showMyboard(@AuthenticationPrincipal MemberContext user){
        return boardService.getMyBoard(user);
    }

    @GetMapping("/api/board/search/{search}")
    public List<BoardResponseDto> searchBoard(@PathVariable String search, Pageable p){
        System.out.println(search);
        return boardService.searchBoard(search, p);
    }


}
