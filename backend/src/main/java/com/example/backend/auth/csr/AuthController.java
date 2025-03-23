package com.example.backend.auth.csr;

import com.example.backend.auth.error.*;
import com.example.backend.person.Person;
import com.example.backend.person.PersonDTO;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth Controller", description = "API for authentication")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "Sign up a new user and return the refresh token and access token in a cookie")
    public ResponseEntity<ApiResponse<Void>> signup(@RequestBody PersonDTO personDTO, HttpServletResponse response) {
        ServiceResult<Void, AuthSignupError> result = authService.signup(personDTO, response);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }

        AuthSignupError error = result.getError();
        return switch (error) {
            case USERNAME_ALREADY_EXISTS, USERNAME_CANNOT_BE_NULL_OR_EMPTY, PASSWORD_CANNOT_BE_NULL_OR_EMPTY ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case STREAK_UPDATE_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @PostMapping("/login")
    @Operation(summary = "Log in a user and return the refresh token and access token in a cookie")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody PersonDTO personDTO, HttpServletResponse response) {
        ServiceResult<Void, AuthLoginError> result = authService.login(personDTO, response);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }
        AuthLoginError error = result.getError();
        return switch (error) {
            case USERNAME_CANNOT_BE_NULL_OR_EMPTY, PASSWORD_CANNOT_BE_NULL_OR_EMPTY ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case INVALID_USERNAME_OR_PASSWORD ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case STREAK_UPDATE_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh the access token using the refresh token")
    public ResponseEntity<ApiResponse<Void>> refresh(@CookieValue("refreshToken") String refreshToken,
                                                     HttpServletResponse response) {
        ServiceResult<Void, AuthRefreshError> result = authService.refresh(refreshToken, response);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }

        AuthRefreshError error = result.getError();
        return switch (error) {
            case INVALID_REFRESH_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case STREAK_UPDATE_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out a user and by returning a refresh token and access token with an expiration date of 0 in a cookie")
    public ResponseEntity<ApiResponse<Void>> logout(@CookieValue("refreshToken") String refreshToken,
                                                    HttpServletResponse response) {
        ServiceResult<Void, AuthLogoutError> result = authService.logout(refreshToken, response);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }

        AuthLogoutError error = result.getError();
        return switch (error) {
            case INVALID_REFRESH_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
        };

    }

    @GetMapping("/me")
    @Operation(summary = "Get the current user information")
    public ResponseEntity<ApiResponse<Person>> me(@CookieValue("accessToken") String accessToken) {
        ServiceResult<Person, AuthMeError> result = authService.me(accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }
        AuthMeError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
        };
    }
}
