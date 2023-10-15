package com.example.testweb.data.repo;

import com.example.testweb.data.userdata.token.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AuthTokenRepo extends JpaRepository<AuthToken, Integer> {
    /*
    * This repository will be similar to what is in amigo's code
    * It will be used to avail multiple users to register
    * It will map users and their tokens in some ways
    * I don't know how and what will be the method yet
    * There also needs to be some way to regenerate the token
    * Since it expires after some time
    * Like a day or a week
    */
}
