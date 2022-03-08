package com.hanghae99.boilerplate.repository;

import com.hanghae99.boilerplate.security.model.RefreshTokenDB;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenDB,String> {


}
