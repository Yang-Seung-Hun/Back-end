package com.hanghae99.boilerplate.board.repository;

import com.hanghae99.boilerplate.security.model.RefreshTokenDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenDB,String> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE from refreshToken r where r.email = :email")
    void deleteToken(@Param("email") String email);
}