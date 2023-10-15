package com.example.testweb.data.repo;

import com.example.testweb.data.userdata.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDataRepo extends JpaRepository<MyUser,Integer> {
}
