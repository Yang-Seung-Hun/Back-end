package com.hanghae99.boilerplate.board.repository;


import com.hanghae99.boilerplate.model.Member;

import java.util.List;

public interface MemberCustomRepository {

    List<Member> findByIdJoinFetch(Long Id);

}
