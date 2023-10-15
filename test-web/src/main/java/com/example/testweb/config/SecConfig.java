package com.example.testweb.config;
import com.example.testweb.config.filter.JwtAuthFilter;
import com.example.testweb.config.service.LogoutService;
import com.example.testweb.config.service.MyUserDetailsService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
@Configuration
@EnableWebSecurity
public class SecConfig {
    @Autowired
    private JwtAuthFilter myFilter;
    @Autowired
    private LogoutHandler logoutHandler;
    @Bean
    @SneakyThrows
    public SecurityFilterChain filterChain(HttpSecurity http){
        http.csrf(csrf->{
            csrf.disable();
        }).cors(cors->{
            cors.configurationSource(configurationSource());
        }).authorizeHttpRequests(req->{
            req.requestMatchers("/error","/rest/v1/*","/auth/v1/**").permitAll();
            req.anyRequest().authenticated();
        }).sessionManagement(session->{
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }).authenticationProvider(authenticationProvider())
                .addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout->{
                    logout.logoutUrl("/auth/v1/logout");
                    logout.addLogoutHandler(logoutHandler);
                    logout.logoutSuccessHandler((request, response, authentication) -> {
                        SecurityContextHolder.clearContext();
                    });
                });
        return http.build();
    }
    @Bean
    public CorsConfigurationSource configurationSource(){
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000/"));
        config.setAllowedMethods(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",config);
        return configurationSource;
    }
    @Bean
    public UserDetailsService userDetailsService(){
        MyUserDetailsService userDetailsService = new MyUserDetailsService();
        return userDetailsService;
    }
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(encoder());
        return authProvider;
    }
    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    @SneakyThrows
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config){
        return config.getAuthenticationManager();
    }
}
