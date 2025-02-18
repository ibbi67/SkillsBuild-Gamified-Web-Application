package com.example.backend.service;

import com.example.backend.dao.FavouriteCourseDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavouriteService {

    private final JwtService jwtService;

    private final UserService userService;

    private final CourseService courseService;

    public FavouriteService(JwtService jwtService, UserService userService, CourseService courseService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.courseService = courseService;
    }

    public ApiResponse<List<Course>> getFavourite(String accessToken) {
        if (!jwtService.verifyToken(accessToken))
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        String username = jwtService.getUserDetails(accessToken).getUsername();
        User user = userService.findByUsername(username);
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        return ApiResponse.success("Favourite courses found", user.getFavouriteCourses());
    }


    public ApiResponse<Void> addFavourite(String accessToken, FavouriteCourseDao favouriteCourseDao) {
        if (!jwtService.verifyToken(accessToken))
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        String username = jwtService.getUserDetails(accessToken).getUsername();
        User user = userService.findByUsername(username);
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        Course course = courseService.getCourseById(favouriteCourseDao.getCourseId());
        user.getFavouriteCourses().add(course);
        userService.save(user);

        return ApiResponse.success("Favourite course added successfully");
    }

    public ApiResponse<Void> removeFavourite(String accessToken, FavouriteCourseDao favouriteCourseDao) {
        if (!jwtService.verifyToken(accessToken))
            return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        String username = jwtService.getUserDetails(accessToken).getUsername();
        User user = userService.findByUsername(username);
        if (user == null) return ApiResponse.failed(HttpStatus.BAD_REQUEST.value(), "Invalid access token");

        Course course = courseService.getCourseById(favouriteCourseDao.getCourseId());
        user.getFavouriteCourses().remove(course);
        userService.save(user);

        return ApiResponse.success("Favourite course removed successfully");
    }
}
