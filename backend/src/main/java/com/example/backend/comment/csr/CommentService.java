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

    // Constants for validation
    private static final int MAX_COMMENT_LENGTH = 1000;
    private static final int MIN_COMMENT_LENGTH = 2;

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
        if (!StringUtils.hasText(commentDTO.getContent())) {
            return ServiceResult.error(CommentCreateError.EMPTY_CONTENT);
        }

        // Validate content length
        String content = commentDTO.getContent().trim();
        if (content.length() < MIN_COMMENT_LENGTH) {
            return ServiceResult.error(CommentCreateError.CONTENT_TOO_SHORT);
        }

        if (content.length() > MAX_COMMENT_LENGTH) {
            return ServiceResult.error(CommentCreateError.CONTENT_TOO_LONG);
        }

        // Check for potentially inappropriate content
        if (containsInappropriateContent(content)) {
            return ServiceResult.error(CommentCreateError.INAPPROPRIATE_CONTENT);
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

        // Check if user is spamming (optional - would need to implement a rate limit)
        if (isUserSpamming(personOptional.get().getId())) {
            return ServiceResult.error(CommentCreateError.RATE_LIMIT_EXCEEDED);
        }

        try {
            Person person = personOptional.get();
            Course course = courseOptional.get();

            // Sanitize the content before saving
            String sanitizedContent = sanitizeContent(content);

            Comment comment = new Comment(sanitizedContent, person, course);
            Comment savedComment = commentRepository.save(comment);
            return ServiceResult.success(savedComment);
        } catch (Exception e) {
            return ServiceResult.error(CommentCreateError.COMMENT_CREATION_FAILED);
        }
    }

    /**
     * Check if content contains inappropriate words or phrases
     */
    private boolean containsInappropriateContent(String content) {
        // Simple implementation - in a real system you might use a more sophisticated approach
        String[] inappropriateWords = {"badword1", "badword2", "badword3"};
        String lowerContent = content.toLowerCase();

        for (String word : inappropriateWords) {
            if (lowerContent.contains(word)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Sanitize content to prevent XSS and other injection attacks
     */
    private String sanitizeContent(String content) {
        // Simple implementation - in a real system you would use a library like OWASP Java HTML Sanitizer
        return content
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * Check if user is posting comments too frequently
     */
    private boolean isUserSpamming(Long userId) {
        // Simple implementation - in a real system you would check recent comments by this user
        // and implement a rate limiting mechanism
        return false; // Placeholder implementation
    }
}