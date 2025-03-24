package com.example.backend.comment.csr;

import com.example.backend.comment.Comment;
import com.example.backend.comment.CommentDTO;
import com.example.backend.comment.error.CommentCreateError;
import com.example.backend.comment.error.CommentGetByCourseError;
import com.example.backend.course.Course;
import com.example.backend.course.csr.CourseRepository;
import com.example.backend.person.Person;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private JWT jwt;

    @InjectMocks
    private CommentService commentService;

    private Course testCourse;
    private Person testPerson;
    private Comment testComment;
    private final Integer courseId = 1;
    private final String accessToken = "valid-token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCourse = new Course();
        testCourse.setId(courseId);
        testCourse.setTitle("Test Course");

        testPerson = new Person();
        testPerson.setUsername("testuser");

        testComment = new Comment();
        testComment.setContent("Test comment");
        testComment.setPerson(testPerson);
        testComment.setCourse(testCourse);
    }

    @Test
    void getCommentsByCourseId_whenCourseExists_returnsComments() {
        // Arrange
        List<Comment> comments = new ArrayList<>();
        comments.add(testComment);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));
        when(commentRepository.findByCourseId(courseId)).thenReturn(comments);

        // Act
        ServiceResult<List<Comment>, CommentGetByCourseError> result = commentService.getCommentsByCourseId(courseId);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        assertEquals("Test comment", result.getData().get(0).getContent());

        verify(courseRepository).findById(courseId);
        verify(commentRepository).findByCourseId(courseId);
    }

    @Test
    void getCommentsByCourseId_whenCourseDoesNotExist_returnsError() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act
        ServiceResult<List<Comment>, CommentGetByCourseError> result = commentService.getCommentsByCourseId(courseId);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(CommentGetByCourseError.COURSE_NOT_FOUND, result.getError());

        verify(courseRepository).findById(courseId);
        verify(commentRepository, never()).findByCourseId(any());
    }

    @Test
    void getCommentsByCourseId_whenExceptionOccurs_returnsError() {
        // Arrange
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));
        when(commentRepository.findByCourseId(courseId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ServiceResult<List<Comment>, CommentGetByCourseError> result = commentService.getCommentsByCourseId(courseId);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(CommentGetByCourseError.GET_COMMENTS_FAILED, result.getError());

        verify(courseRepository).findById(courseId);
        verify(commentRepository).findByCourseId(courseId);
    }

    @Test
    void addComment_whenValidRequest_returnsCreatedComment() {
        // Arrange
        CommentDTO commentDTO = new CommentDTO("Test comment", courseId);

        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.of(testPerson));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        // Act
        ServiceResult<Comment, CommentCreateError> result = commentService.addComment(commentDTO, accessToken);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Test comment", result.getData().getContent());

        verify(jwt).getPersonFromToken(accessToken);
        verify(courseRepository).findById(courseId);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_whenInvalidToken_returnsUnauthorizedError() {
        // Arrange
        CommentDTO commentDTO = new CommentDTO("Test comment", courseId);

        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.empty());

        // Act
        ServiceResult<Comment, CommentCreateError> result = commentService.addComment(commentDTO, accessToken);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(CommentCreateError.UNAUTHORIZED, result.getError());

        verify(jwt).getPersonFromToken(accessToken);
        verify(courseRepository, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_whenCourseDoesNotExist_returnsCourseNotFoundError() {
        // Arrange
        CommentDTO commentDTO = new CommentDTO("Test comment", courseId);

        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.of(testPerson));
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        // Act
        ServiceResult<Comment, CommentCreateError> result = commentService.addComment(commentDTO, accessToken);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(CommentCreateError.COURSE_NOT_FOUND, result.getError());

        verify(jwt).getPersonFromToken(accessToken);
        verify(courseRepository).findById(courseId);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_whenSaveFails_returnsCreationFailedError() {
        // Arrange
        CommentDTO commentDTO = new CommentDTO("Test comment", courseId);

        when(jwt.getPersonFromToken(accessToken)).thenReturn(Optional.of(testPerson));
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(testCourse));
        when(commentRepository.save(any(Comment.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ServiceResult<Comment, CommentCreateError> result = commentService.addComment(commentDTO, accessToken);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals(CommentCreateError.COMMENT_CREATION_FAILED, result.getError());

        verify(jwt).getPersonFromToken(accessToken);
        verify(courseRepository).findById(courseId);
        verify(commentRepository).save(any(Comment.class));
    }
}