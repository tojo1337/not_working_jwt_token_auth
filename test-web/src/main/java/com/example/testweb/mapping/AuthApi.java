package com.example.testweb.mapping;

import com.example.testweb.config.service.AuthService;
import com.example.testweb.config.service.JwtService;
import com.example.testweb.config.service.MyUserDetailsService;
import com.example.testweb.data.mockdata.SampleData;
import com.example.testweb.data.userdata.AuthResponse;
import com.example.testweb.data.userdata.AuthUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/auth/v1")
public class AuthApi {
    private Logger log = Logger.getLogger(this.getClass().getName());
    @Autowired
    private AuthService authService;
    @PostMapping("/register")
    public AuthResponse addNewUser(@RequestBody AuthUserRequest authReq){
        log.info("Before sending to registration service : "+authReq.getUsername());
        AuthResponse response = authService.register(authReq);
        log.info("After registration : "+response.toString());
        return response;
    }
    @PostMapping("/token")
    public AuthResponse authTokenGenerate(@RequestBody AuthUserRequest user){
        AuthResponse response = authService.authenticate(user);
        return response;
    }
    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response){
        authService.refreshToken(request,response);
    }
}
