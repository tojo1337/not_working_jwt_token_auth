package com.example.testweb.data.userdata;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
}