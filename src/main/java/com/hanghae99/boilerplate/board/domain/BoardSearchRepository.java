package com.hanghae99.boilerplate.board.domain;

import com.hanghae99.boilerplate.board.dto.BoardSearchDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardSearchRepository {
    List<BoardSearchDto> searchByBoard(String name, Pageable pageable);
}
