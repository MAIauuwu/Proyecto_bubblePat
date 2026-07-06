package com.bubblepat.backend.dto;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String name;
    private String email;
    private Integer themeHue;
}
