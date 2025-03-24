package com.example.backend.profile.csr;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.profile.ProfileDTO;
import com.example.backend.profile.error.ProfileUpdateError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/profile")
@Tag(name = "Profile Controller", description = "API for updating user profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Void>> update(@CookieValue("accessToken") String accessToken, @RequestBody ProfileDTO profileDTO) {
        ServiceResult<Void, ProfileUpdateError> result = profileService.update(accessToken, profileDTO);
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(null));
        }
        ProfileUpdateError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case PROFILE_UPDATE_FAILED -> new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }   
}
