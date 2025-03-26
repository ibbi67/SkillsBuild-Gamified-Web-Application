package com.example.backend.friend;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendResponseDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String avatarLink;
}
