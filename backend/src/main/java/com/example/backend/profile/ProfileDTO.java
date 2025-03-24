package com.example.backend.profile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarLink;
}
