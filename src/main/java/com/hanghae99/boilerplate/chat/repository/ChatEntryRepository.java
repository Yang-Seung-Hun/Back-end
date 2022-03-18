package com.hanghae99.boilerplate.chat.repository;

import com.hanghae99.boilerplate.chat.model.ChatEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatEntryRepository extends JpaRepository<ChatEntry, Long> {
}
