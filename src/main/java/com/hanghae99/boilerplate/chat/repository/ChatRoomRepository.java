package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.model.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {


}