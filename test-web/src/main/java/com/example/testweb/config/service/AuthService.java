package com.example.testweb.config.service;

import com.example.testweb.data.repo.AuthTokenRepo;
import com.example.testweb.data.repo.TokenUserMapperRepo;
import com.example.testweb.data.userdata.AuthResponse;
import com.example.testweb.data.userdata.AuthUserRequest;
import com.example.testweb.data.userdata.MyUser;
import com.example.testweb.data.userdata.MyUserDetails;
import com.example.testweb.data.userdata.token.AuthToken;
import com.example.testweb.data.userdata.token.TokenUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class AuthService {
    private Logger log = Logger.getLogger(this.getClass().getName());
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private AuthTokenRepo tokenRepo;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenUserMapperRepo tokenUserRepo;
    public AuthResponse register(AuthUserRequest user){
        log.info("User data input : "+user.getUsername());
        userDetailsService.save(user);
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        log.info("Returned user information from user details service : "+userDetails.getUsername());
        String jwtToken = jwtService.generateToken(userDetails);
        log.info("Jwt Token : "+jwtToken);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        saveUserToken((MyUserDetails) userDetails,jwtToken);
        return AuthResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }
    public AuthResponse authenticate(AuthUserRequest user){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
        var userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        revokeAllUserToken((MyUserDetails) userDetails);
        saveUserToken((MyUserDetails) userDetails, jwtToken);
        return AuthResponse.builder().accessToken(jwtToken).refreshToken(refreshToken).build();
    }
    private void revokeAllUserToken(MyUserDetails user){
        List<AuthToken> allTokens = tokenRepo.findAll();
        List<AuthToken> validTokens = new ArrayList<>();
        if(allTokens.isEmpty()){
            return;
        }
        for(AuthToken token:allTokens){
            if(token.getUser().getUsername().equals(user.getUsername()) && !token.isRevoked() && !token.isExpired()){
                validTokens.add(token);
            }
        }
        validTokens.forEach(token->{
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepo.saveAll(validTokens);
    }
    private void saveUserToken(MyUserDetails user, String jwtToken){
        TokenUser tokenUser = TokenUser.builder().id(user.getId()).username(user.getUsername()).build();
        AuthToken token = AuthToken.builder()
                .user(tokenUser)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenUserRepo.save(tokenUser);
        tokenRepo.save(token);
    }
    @SneakyThrows
    public void refreshToken(HttpServletRequest request, HttpServletResponse response){
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader==null||!authHeader.startsWith("Bearer ")){
            return;
        }
        String refreshToken = authHeader.substring(7);
        String name = jwtService.extractUsername(refreshToken);
        MyUserDetails user = (MyUserDetails) userDetailsService.loadUserByUsername(name);
        if(jwtService.isTokenValid(refreshToken,user)){
            String accessToken = jwtService.generateToken(user);
            this.revokeAllUserToken(user);
            this.saveUserToken(user,accessToken);
            AuthResponse authResponce = AuthResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
            new ObjectMapper().writeValue(response.getOutputStream(),authResponce);
        }
    }
}
