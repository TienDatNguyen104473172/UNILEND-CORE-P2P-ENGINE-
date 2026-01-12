package com.unilend.dto.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String fullName;

    public JwtResponse(String accessToken, Long id, String email, String fullName) {
        this.token = accessToken;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
    }
}
