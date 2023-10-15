package com.example.testweb.config.filter;

import com.example.testweb.config.service.JwtService;
import com.example.testweb.config.service.MyUserDetailsService;
import com.example.testweb.data.repo.AuthTokenRepo;
import com.example.testweb.data.userdata.token.AuthToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    @Autowired
    private MyUserDetailsService userDetailsService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthTokenRepo tokenRepo;
    @Override
    @SneakyThrows
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) {
        if(request.getServletPath().contains("/auth/v1/")){
            System.out.println("[*]JwtAuthFilter will not recognize \"/auth/v1\" as part of it\'s range");
            filterChain.doFilter(request,response);
            return;
        }else {
            String authHeader = request.getHeader("Authorization");
            if(authHeader==null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request,response);
                return;
            }
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            if(username!=null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                List<AuthToken> tokens = tokenRepo.findAll();
                AuthToken userToken = null;
                for(AuthToken tok:tokens){
                    if(tok.getToken().equals(token)){
                        userToken = tok;
                    }
                }
                boolean isTokenValid = false;
                if(!userToken.isExpired() && !userToken.isRevoked()){
                    isTokenValid = true;
                }
                if(jwtService.isTokenValid(token,userDetails) && isTokenValid){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request,response);
        }
    }
}