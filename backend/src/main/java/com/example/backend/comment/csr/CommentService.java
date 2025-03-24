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
        // Validate authentication
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