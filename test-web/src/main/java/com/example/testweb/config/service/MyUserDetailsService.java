package com.example.testweb.config.service;

import com.example.testweb.data.repo.UserDataRepo;
import com.example.testweb.data.userdata.AuthUserRequest;
import com.example.testweb.data.userdata.MyUser;
import com.example.testweb.data.userdata.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private Logger log = Logger.getLogger(this.getClass().getName());
    @Autowired
    private UserDataRepo repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<MyUser> li = repo.findAll();
        MyUserDetails details = new MyUserDetails();
        for(MyUser user:li){
            if(user.getUsername().equals(username)){
                details.setUser(user);
            }else {
                throw new UsernameNotFoundException(username);
            }
        }
        log.info("Load user by user name : "+details.getUsername());
        return details;
    }
    public void save(AuthUserRequest userRequest){
        log.info("Before registration : "+userRequest.getUsername());
        MyUser user = new MyUser();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder().encode(userRequest.getPassword()));
        log.info("After registration : "+user.getUsername());
        repo.save(user);
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
