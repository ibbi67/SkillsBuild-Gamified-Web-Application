package com.example.backend.friend.csr;

import com.example.backend.friend.FriendDTO;
import com.example.backend.friend.FriendResponseDTO;
import com.example.backend.friend.error.FriendAddError;
import com.example.backend.friend.error.FriendGetAllError;
import com.example.backend.friend.error.FriendRemoveError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@Tag(name = "Friend Controller", description = "API for managing friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @Operation(summary = "Get all friends")
    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendResponseDTO>>> getAllFriends(@CookieValue(name = "accessToken") String accessToken) {
        ServiceResult<List<FriendResponseDTO>, FriendGetAllError> result = friendService.getAllFriends(accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.OK);
        }

        FriendGetAllError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
        };
    }

    @Operation(summary = "Add a friend")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addFriend(
            @CookieValue(name = "accessToken") String accessToken,
            @RequestBody FriendDTO friendDTO) {
        ServiceResult<Void, FriendAddError> result = friendService.addFriend(accessToken, friendDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }

        FriendAddError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case PERSON_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
            case ALREADY_FRIENDS, CANNOT_ADD_SELF ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case FRIEND_ADD_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @Operation(summary = "Remove a friend")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFriend(
            @CookieValue(name = "accessToken") String accessToken,
            @RequestBody FriendDTO friendDTO) {
        ServiceResult<Void, FriendRemoveError> result = friendService.removeFriend(accessToken, friendDTO);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.OK);
        }

        FriendRemoveError error = result.getError();
        return switch (error) {
            case INVALID_ACCESS_TOKEN ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case PERSON_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
            case NOT_FRIENDS ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case FRIEND_REMOVE_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}