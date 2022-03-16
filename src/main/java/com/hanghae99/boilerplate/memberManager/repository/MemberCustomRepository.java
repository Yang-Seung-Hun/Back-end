package com.hanghae99.boilerplate.memberManager.repository;


import com.hanghae99.boilerplate.memberManager.model.Member;

import java.util.List;

public interface MemberCustomRepository {

    List<Member> findByIdJoinFetch(Long Id);

}
