package com.example.backend.controller;

import com.example.backend.dao.FavouriteCourseDao;
import com.example.backend.domain.ApiResponse;
import com.example.backend.domain.Course;
import com.example.backend.service.FavouriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favourite")
public class FavouriteController {

    private final FavouriteService favouriteService;

    public FavouriteController(FavouriteService favouriteService) {this.favouriteService = favouriteService;}

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Course>>> getFavourite(@CookieValue(value = "access_token", required = false) String accessToken) {
        ApiResponse<List<Course>> getFavouriteResponse = favouriteService.getFavourite(accessToken);
        return ResponseEntity.status(getFavouriteResponse.getStatus()).body(getFavouriteResponse);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addFavourite(@CookieValue(value = "access_token", required = false) String accessToken, @RequestBody FavouriteCourseDao favouriteCourseDao) {
        ApiResponse<Void> addFavouriteResponse = favouriteService.addFavourite(accessToken, favouriteCourseDao);
        return ResponseEntity.status(addFavouriteResponse.getStatus()).body(addFavouriteResponse);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ApiResponse<Void>> removeFavourite(@CookieValue(value = "access_token", required = false) String accessToken, @RequestBody FavouriteCourseDao favouriteCourseDao) {
        ApiResponse<Void> removeFavouriteResponse = favouriteService.removeFavourite(accessToken, favouriteCourseDao);
        return ResponseEntity.status(removeFavouriteResponse.getStatus()).body(removeFavouriteResponse);
    }

}
