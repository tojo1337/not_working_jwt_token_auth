package com.example.testweb.data.repo;

import com.example.testweb.data.userdata.token.TokenUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenUserMapperRepo extends JpaRepository<TokenUser,Integer> {
}
