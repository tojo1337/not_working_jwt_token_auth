package com.example.testweb.config.service;

import com.example.testweb.data.repo.AuthTokenRepo;
import com.example.testweb.data.userdata.token.AuthToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogoutService implements LogoutHandler {
    @Autowired
    private AuthTokenRepo tokenRepo;
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //The logout function based on jwt token will be implemented in here
        String authHeader = request.getHeader("Authorization");
        if(authHeader==null||!authHeader.startsWith("Bearer ")){
            return;
        }
        String jwtToken = authHeader.substring(7);
        List<AuthToken> tokens = tokenRepo.findAll();
        for(AuthToken token:tokens){
            if(token.getToken().equals(jwtToken)){
                token.setExpired(true);
                token.setRevoked(true);
                tokenRepo.save(token);
            }
        }
    }
}
