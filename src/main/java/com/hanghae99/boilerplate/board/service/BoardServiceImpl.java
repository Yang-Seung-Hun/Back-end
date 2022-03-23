package com.hanghae99.boilerplate.board.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanghae99.boilerplate.board.domain.*;
import com.hanghae99.boilerplate.board.dto.*;
import com.hanghae99.boilerplate.memberManager.model.Member;
import com.hanghae99.boilerplate.memberManager.repository.MemberRepository;
import com.hanghae99.boilerplate.noti.service.FCMService;
import com.hanghae99.boilerplate.security.model.MemberContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final RecommendBoardRepository recommendBoardRepository;
    private final VoteRepository voteRepository;
    private final RecommendCommentRepository recommendCommentRepository;
    private final MemberRepository memberRepository;
    private final MyBoardRepository myBoardRepository;
    private final ReplyRepository replyRepository;

    private final FCMService fcmService;
    private final BoardSearchRepository boardSearchRepository;

    @Transactional
    @Override
    @CacheEvict(cacheNames = "board", allEntries = true) // , key = "#username")
    public void createBoard(BoardRequestDto boardRequestDto, MemberContext user){
        Optional<Member> member = memberRepository.findById(user.getMemberId());
        Board board = boardRepository.save(boardRequestDto.toEntity(member.get()));
    }


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "board")
    public List<BoardResponseDto> showAllBoard(Pageable pageable) {

        List<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc(pageable); // findAll();
        return boards.stream()
                .map(Board::toCreatedDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    //@Cacheable(cacheNames = "board", key = "#boardId")
    public BoardResponseDto showBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).get();
        return board.toCreatedDto();
    }


    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "board", key = "#categoryName") // + '::' + #pageNo")
    public List<BoardResponseDto> showBoardByCategory(String categoryName, Pageable pageable) {
        List<Board> boards = boardRepository.findAllByCategory(categoryName); // findAll();
        return boards.stream()
                .map(Board::toCreatedDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(cacheNames = "board", allEntries = true)
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    @Override
    @Transactional
    public void recommendBoard(Long boardId, MemberContext user) {
        Optional<Board> board = boardRepository.findById(boardId);
        Optional<Member> member = memberRepository.findById(user.getMemberId());
        Optional<RecommendBoard> recommendBoard = recommendBoardRepository.findByBoardAndMember(board.get(), member.get());

        if (member.get().getRecommendBoards().isEmpty()){
            RecommendBoard recommendBoard1 =  RecommendBoard.builder()
                    .member(member.get())
                    .board(board.get())
                    .build();

            recommendBoardRepository.save(recommendBoard1);
            board.get().addRecommendCount();
        }else{
            recommendBoardRepository.deleteById(recommendBoard.get().getId());
            board.get().subtractRecommendCount();

        }
    }

    @Override
    @Transactional
    public void agreeBoard(Long boardId, MemberContext user) {
        Optional<Member> member = memberRepository.findById(user.getMemberId());
        Optional<Board> board = boardRepository.findById(boardId);
        Optional<Vote> vote = voteRepository.findByBoardAndMember(board.get(), member.get());

        if (vote.isEmpty()){
            Vote vote1 =  Vote.builder()
                    .member(member.get())
                    .board(board.get())
                    .agreed(Boolean.TRUE)
                    .build();
            voteRepository.save(vote1);
            board.get().addAgreeCount();
        }else if (vote.get().getAgreed()){
            voteRepository.deleteById(vote.get().getId());
            board.get().subtractAgreeCount();
        }else{
            vote.get().setAgreed(Boolean.TRUE);
            board.get().addAgreeCount();
            board.get().subtractDisagreeCount();
        }
    }

    @Override
    @Transactional
    public void disagreeBoard(Long boardId, MemberContext user) {
        Optional<Board> board = boardRepository.findById(boardId);
        Optional<Member> member = memberRepository.findById(user.getMemberId());
        Optional<Vote> vote = voteRepository.findByBoardAndMember(board.get(), member.get());

        if (vote.isEmpty()){
            Vote vote1 =  Vote.builder()
                    .member(member.get())
                    .board(board.get())
                    .agreed(Boolean.FALSE)
                    .build();
            voteRepository.save(vote1);
            board.get().addDisagreeCount();
        }else if (vote.get().getAgreed()){
            vote.get().setAgreed(Boolean.FALSE);
            board.get().addDisagreeCount();
            board.get().subtractAgreeCount();
        }else{
            voteRepository.deleteById(vote.get().getId());
            board.get().subtractDisagreeCount();
        }

    }

    @Override
    @Transactional
    public void createComment(CommentRequestDto commentRequestDto, MemberContext user) {

        Board board = boardRepository.findById(commentRequestDto.getBoardId()).orElse(new Board());
        Optional<Member> member = memberRepository.findById(user.getMemberId());
        Comment comment = Comment.builder()
                .board(board)
                .member(member.get())
                .content(commentRequestDto.getContent())
                .createdAt(LocalDateTime.now()).build();

        commentRepository.save(comment);

    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> showComments(Long boardId){


        List<Board> board = boardRepository.findByIdJoinFetch(boardId);

        //N + 1 !!!
        return board.get(0).getComments().stream().map(Comment::toDto).collect(Collectors.toList());

        //commentRepository.findAllByBoardIdOrderByCreatedAtDesc(boardId).stream().map(Comment::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId){

        commentRepository.deleteById(commentId);
    }



    @Override
    @Transactional
    public void recommendComment(Long commentId, MemberContext user) {
        Optional<Member> member = memberRepository.findById(user.getMemberId());

        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<RecommendComment> recommendComment = recommendCommentRepository.findByCommentAndMember(comment.get(), member.get());

        if (recommendComment.isEmpty()){
            RecommendComment recommendComment1 =  RecommendComment.builder()
                    .member(member.get())
                    .comment(comment.get())
                    .build();

            recommendCommentRepository.save(recommendComment1);
            comment.get().addRecommendCount();
        }else{
            recommendCommentRepository.deleteById(recommendComment.get().getId());
            comment.get().subtractRecommendCount();
        }
    }

    @Override
    @Transactional
    public void createReply(Long commentId, ReplyRequestDto replyRequestDto, MemberContext user) throws ExecutionException, InterruptedException, JsonProcessingException {
        Optional<Comment> comment = commentRepository.findById(commentId);
        Optional<Member> member = memberRepository.findById(user.getMemberId());

        Reply reply = Reply.builder()
                .comment(comment.get())
                .member(member.get())
                .createdAt(LocalDateTime.now())
                .content(replyRequestDto.getContent())
                .build();
        replyRepository.save(reply);

        System.out.println(reply+ "저장");

        //push
        System.out.println(comment.get().getMember().getNickname());
        fcmService.sendMessageTo(comment.get().getMember().getId(), "댓글 Notification",  comment.get().getContent() + "에 대댓글이 달렸습니다");
    }

    @Override
    @Transactional
    public List<ReplyResponseDto> showReplies(Long commentId) {
        return commentRepository.findById(commentId).get().getReplies().stream()
                .map(Reply::toDto).collect(Collectors.toList());

    }

    @Override
    public List<BoardResponseDto> getMyBoard(MemberContext user) {
        //N+1
        System.out.println(memberRepository.findByIdJoinFetch(user.getMemberId()).get(0).getMyBoards().get(0).getBoard().getContent());

        return memberRepository.findByIdJoinFetch(user.getMemberId())
                .get(0)
                .getMyBoards()
                .stream()
                .map(myBoard ->
                    boardRepository.findById(myBoard.getBoard().getId()).get().toCreatedDto()
                ).collect(Collectors.toList());
    }

    @Override
    public void setMyBoard(Long boardId, MemberContext user) {
        Board board = boardRepository.findById(boardId).get();
        Optional<Member> member = memberRepository.findById(user.getMemberId());
        MyBoard myBoard = MyBoard.builder()
                        .board(board)
                        .member(member.get()
                                )
                        .build();
        myBoardRepository.save(myBoard);
    }

    @Override
    public List<BoardResponseDto> searchBoard(String content, Pageable pageable) {
        return boardSearchRepository.searchByBoard(content, pageable)
                .stream()
                .map(boardSearchDto -> {

                    Optional<Member> member = memberRepository.findById(boardSearchDto.getMember_id());
                    return BoardResponseDto.builder()
                            .id(boardSearchDto.getId())
                            .title(boardSearchDto.getTitle())
                            .nickname(member.get().getNickname())
                            .profileImageUrl(member.get().getProfileImageUrl())
                            .content(boardSearchDto.getContent())
                            .imageUrl(boardSearchDto.getImage_url())
                            .agreeCount(boardSearchDto.getAgree_count())
                            .disagreeCount(boardSearchDto.getDisagree_count())
                            .recommendCount(boardSearchDto.getRecommend_count())
                            .createdAt(boardSearchDto.getCreated_at())
                            .category(boardSearchDto.getCategory())
                            .build();
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<BoardResponseDto> getMyWrittenBoard(MemberContext user) {
        Optional<Member> member = memberRepository.findById(user.getMemberId());

        return boardRepository.findAllByMember(member.get()).stream()
                .map(Board::toCreatedDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BoardResponseDto> getMyComment(MemberContext user) {
        Optional<Member> member = memberRepository.findById(user.getMemberId());

        return commentRepository.findAllByMember(member.get())
                .stream()
                .map(comment ->
                        boardRepository.findById(comment.getBoard().getId())
                                .get().toCreatedDto()
                )
                .collect(Collectors.toList());

        //return null;
    }

    @Override
    public void recommendReply(Long replyId, MemberContext user){
        Optional<Member> member = memberRepository.findById(user.getMemberId());

        Optional<Reply> reply = replyRepository.findById(replyId);
        Optional<RecommendReply> recommendReply = recommendReplyRepository.findByReplyAndMember(reply.get(), member.get());

        if (recommendReply.isEmpty()){
            RecommendReply recommendReply1 =  RecommendReply.builder()
                    .member(member.get())
                    .reply(reply.get())
                    .build();
            reply.get().addRecommendCount();
            System.out.println("추천수 더하기");
            System.out.println(reply.get().getRecommendCount());
            recommendReplyRepository.save(recommendReply1);
        }else{
            reply.get().subtractRecommendCount();
            System.out.println("추천수 빼기");
            System.out.println(reply.get().getRecommendCount());
            recommendReplyRepository.deleteById(recommendReply.get().getId());

        }

    }

}
