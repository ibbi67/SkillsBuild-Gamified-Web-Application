package com.example.backend.comment.csr;

import com.example.backend.comment.Comment;
import com.example.backend.comment.CommentDTO;
import com.example.backend.comment.error.CommentCreateError;
import com.example.backend.comment.error.CommentGetByCourseError;
import com.example.backend.person.Person;
import com.example.backend.util.ApiResponse;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class CommentControllerTest {

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private Comment testComment;
    private CommentDTO testCommentDTO;
    private final Integer courseId = 1;
    private final String accessToken = "valid-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Use a constructor without ID for Person
        Person testPerson = new Person();
        testPerson.setUsername("testuser");

        testComment = new Comment();
        testComment.setContent("Test comment");
        testComment.setContent("Test comment");
        testComment.setPerson(testPerson);
        testComment.setCreatedAt(LocalDateTime.now());

        testCommentDTO = new CommentDTO("Test comment", courseId);
    }

    @Test
    void getCommentsByCourseId_whenSuccess_returnsOkWithComments() {
        // Arrange
        List<Comment> comments = new ArrayList<>();
        comments.add(testComment);

        when(commentService.getCommentsByCourseId(courseId)).thenReturn(
                ServiceResult.success(comments)
        );

        // Act
        ResponseEntity<ApiResponse<List<Comment>>> response = commentController.getCommentsByCourseId(courseId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(comments, response.getBody().getData());
    }

    @Test
    void getCommentsByCourseId_whenCourseNotFound_returnsNotFound() {
        // Arrange
        when(commentService.getCommentsByCourseId(courseId)).thenReturn(
                ServiceResult.error(CommentGetByCourseError.COURSE_NOT_FOUND)
        );

        // Act
        ResponseEntity<ApiResponse<List<Comment>>> response = commentController.getCommentsByCourseId(courseId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(CommentGetByCourseError.COURSE_NOT_FOUND.getMessage(), response.getBody().getMessage());
    }

    @Test
    void getCommentsByCourseId_whenGetCommentsFailed_returnsInternalServerError() {
        // Arrange
        when(commentService.getCommentsByCourseId(courseId)).thenReturn(
                ServiceResult.error(CommentGetByCourseError.GET_COMMENTS_FAILED)
        );

        // Act
        ResponseEntity<ApiResponse<List<Comment>>> response = commentController.getCommentsByCourseId(courseId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(CommentGetByCourseError.GET_COMMENTS_FAILED.getMessage(), response.getBody().getMessage());
    }

    @Test
    void addComment_whenSuccess_returnsCreatedWithComment() {
        // Arrange
        when(commentService.addComment(eq(testCommentDTO), eq(accessToken))).thenReturn(
                ServiceResult.success(testComment)
        );

        // Act
        ResponseEntity<ApiResponse<Comment>> response = commentController.addComment(testCommentDTO, accessToken);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testComment, response.getBody().getData());
    }

    @Test
    void addComment_whenUnauthorized_returnsUnauthorized() {
        // Arrange
        when(commentService.addComment(any(), eq(accessToken))).thenReturn(
                ServiceResult.error(CommentCreateError.UNAUTHORIZED)
        );

        // Act
        ResponseEntity<ApiResponse<Comment>> response = commentController.addComment(testCommentDTO, accessToken);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(CommentCreateError.UNAUTHORIZED.getMessage(), response.getBody().getMessage());
    }

    @Test
    void addComment_whenCourseNotFound_returnsNotFound() {
        // Arrange
        when(commentService.addComment(any(), eq(accessToken))).thenReturn(
                ServiceResult.error(CommentCreateError.COURSE_NOT_FOUND)
        );

        // Act
        ResponseEntity<ApiResponse<Comment>> response = commentController.addComment(testCommentDTO, accessToken);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(CommentCreateError.COURSE_NOT_FOUND.getMessage(), response.getBody().getMessage());
    }

    @Test
    void addComment_whenCreationFailed_returnsInternalServerError() {
        // Arrange
        when(commentService.addComment(any(), eq(accessToken))).thenReturn(
                ServiceResult.error(CommentCreateError.COMMENT_CREATION_FAILED)
        );

        // Act
        ResponseEntity<ApiResponse<Comment>> response = commentController.addComment(testCommentDTO, accessToken);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(CommentCreateError.COMMENT_CREATION_FAILED.getMessage(), response.getBody().getMessage());
    }
}