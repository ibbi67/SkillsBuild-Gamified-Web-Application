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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final CourseRepository courseRepository;
    private final JWT jwt;

    public CommentService(CommentRepository commentRepository, CourseRepository courseRepository, JWT jwt) {
        this.commentRepository = commentRepository;
        this.courseRepository = courseRepository;
        this.jwt = jwt;
    }

    public ServiceResult<List<Comment>, CommentGetByCourseError> getCommentsByCourseId(Integer courseId) {
        // Validate course ID
        if (courseId == null || courseId <= 0) {
            return ServiceResult.error(CommentGetByCourseError.INVALID_COURSE_ID);
        }

        // Validate course exists
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            return ServiceResult.error(CommentGetByCourseError.COURSE_NOT_FOUND);
        }

        try {
            List<Comment> comments = commentRepository.findByCourseId(courseId);
            return ServiceResult.success(comments);
        } catch (Exception e) {
            return ServiceResult.error(CommentGetByCourseError.GET_COMMENTS_FAILED);
        }
    }

    public ServiceResult<Comment, CommentCreateError> addComment(CommentDTO commentDTO, String accessToken) {
        // Validate DTO
        if (commentDTO == null) {
            return ServiceResult.error(CommentCreateError.INVALID_REQUEST);
        }

        // Validate content
        if (commentDTO.getContent() == null || !StringUtils.hasText(commentDTO.getContent())) {
            return ServiceResult.error(CommentCreateError.EMPTY_CONTENT);
        }

        // Validate content length
        if (commentDTO.getContent().trim().length() < 2) {
            return ServiceResult.error(CommentCreateError.CONTENT_TOO_SHORT);
        }

        if (commentDTO.getContent().length() > 1000) {
            return ServiceResult.error(CommentCreateError.CONTENT_TOO_LONG);
        }

        // Validate course ID
        if (commentDTO.getCourseId() == null || commentDTO.getCourseId() <= 0) {
            return ServiceResult.error(CommentCreateError.INVALID_COURSE_ID);
        }

        // Validate authentication
        if (accessToken == null || accessToken.isEmpty()) {
            return ServiceResult.error(CommentCreateError.UNAUTHORIZED);
        }

        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(CommentCreateError.UNAUTHORIZED);
        }

        // Validate course exists
        Optional<Course> courseOptional = courseRepository.findById(commentDTO.getCourseId());
        if (courseOptional.isEmpty()) {
            return ServiceResult.error(CommentCreateError.COURSE_NOT_FOUND);
        }

        try {
            Person person = personOptional.get();
            Course course = courseOptional.get();
            Comment comment = new Comment(commentDTO.getContent(), person, course);
            Comment savedComment = commentRepository.save(comment);
            return ServiceResult.success(savedComment);
        } catch (Exception e) {
            return ServiceResult.error(CommentCreateError.COMMENT_CREATION_FAILED);
        }
    }
}