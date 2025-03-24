package com.example.backend.comment.csr;

import com.example.backend.comment.Comment;
import com.example.backend.comment.CommentDTO;
import com.example.backend.comment.error.CommentCreateError;
import com.example.backend.comment.error.CommentGetByCourseError;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@Tag(name = "Comment Controller", description = "API for comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "Get comments by course ID")
    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentsByCourseId(@PathVariable Integer courseId) {
        ServiceResult<List<Comment>, CommentGetByCourseError> result = commentService.getCommentsByCourseId(courseId);
        if (result.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success(result.getData()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failed("Failed to get comments"));
    }

    @Operation(summary = "Add a comment to a course")
    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> addComment(
            @RequestBody CommentDTO commentDTO,
            @CookieValue(value = "accessToken", required = false) String accessToken
    ) {
        ServiceResult<Comment, CommentCreateError> result = commentService.addComment(commentDTO, accessToken);
        if (result.isSuccess()) {
            return new ResponseEntity<>(ApiResponse.success(result.getData()), HttpStatus.CREATED);
        }

        CommentCreateError error = result.getError();
        return switch (error) {
            case INVALID_REQUEST, EMPTY_CONTENT, INVALID_COURSE_ID ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.BAD_REQUEST);
            case UNAUTHORIZED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.UNAUTHORIZED);
            case COURSE_NOT_FOUND ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.NOT_FOUND);
            case COMMENT_CREATION_FAILED ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            default ->
                    new ResponseEntity<>(ApiResponse.failed(error.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }
}