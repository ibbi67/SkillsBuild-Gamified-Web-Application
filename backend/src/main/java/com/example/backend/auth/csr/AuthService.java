package com.example.backend.auth.csr;

import com.example.backend.auth.error.*;
import com.example.backend.person.Person;
import com.example.backend.person.PersonDTO;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AuthService {

    private final PersonService personService;
    private final JWT jwt;

    public AuthService(PersonService personService, JWT jwt) {
        this.personService = personService;
        this.jwt = jwt;
    }

    public ServiceResult<Void, AuthUpdateStreakError> updateStreak(Person person) {
        LocalDate today = LocalDate.now();

        if (ChronoUnit.DAYS.between(person.getLastLoginDate(), today) == 0) {
            // User has already logged in today, no need to update anything
            return ServiceResult.success(null);

        } else if (ChronoUnit.DAYS.between(person.getLastLoginDate(), today) == 1) {
            // User logged in yesterday, increment streak
            person.setStreak(person.getStreak() + 1);

        } else if (ChronoUnit.DAYS.between(person.getLastLoginDate(), today) > 1) {
            // User has not logged in for more than a day, reset streak
            person.setStreak(1);
        }
        person.setLastLoginDate(today);
        Optional<Person> personOptional = personService.save(person);
        if (personOptional.isPresent()) {
            return ServiceResult.success(null);
        }
        return ServiceResult.error(AuthUpdateStreakError.STREAK_UPDATE_FAILED);
    }

    public ServiceResult<Void, AuthSignupError> signup(PersonDTO personDTO, HttpServletResponse response) {
        String username = personDTO.getUsername();
        String password = personDTO.getPassword();

        if (username == null || username.isEmpty()) {
            return ServiceResult.error(AuthSignupError.USERNAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (password == null || password.isEmpty()) {
            return ServiceResult.error(AuthSignupError.PASSWORD_CANNOT_BE_NULL_OR_EMPTY);
        }
        Optional<Person> personOptional = personService.findByUsername(personDTO.getUsername());
        if (personOptional.isPresent()) {
            return ServiceResult.error(AuthSignupError.USERNAME_ALREADY_EXISTS);
        }
        personOptional = personService.saveNewPerson(personDTO);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(AuthSignupError.USERNAME_ALREADY_EXISTS);
        }

        Person person = personOptional.get();
        ServiceResult<Void, AuthUpdateStreakError> updateStreakResult = updateStreak(person);
        if (!updateStreakResult.isSuccess()) {
            return ServiceResult.error(AuthSignupError.STREAK_UPDATE_FAILED);
        }

        jwt.generateRefreshTokenCookie(response, username);
        jwt.generateAccessTokenCookie(response, username);
        return ServiceResult.success(null);
    }

    public ServiceResult<Void, AuthLoginError> login(PersonDTO personDTO, HttpServletResponse response) {
        String username = personDTO.getUsername();
        String password = personDTO.getPassword();

        if (username == null || username.isEmpty()) {
            return ServiceResult.error(AuthLoginError.USERNAME_CANNOT_BE_NULL_OR_EMPTY);
        }
        if (password == null || password.isEmpty()) {
            return ServiceResult.error(AuthLoginError.PASSWORD_CANNOT_BE_NULL_OR_EMPTY);
        }

        Optional<Person> personOptional = personService.verifyPassword(personDTO);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(AuthLoginError.INVALID_USERNAME_OR_PASSWORD);
        }

        Person person = personOptional.get();
        ServiceResult<Void, AuthUpdateStreakError> updateStreakResult = updateStreak(person);
        if (!updateStreakResult.isSuccess()) {
            return ServiceResult.error(AuthLoginError.STREAK_UPDATE_FAILED);
        }

        jwt.generateRefreshTokenCookie(response, username);
        jwt.generateAccessTokenCookie(response, username);
        return ServiceResult.success(null);
    }

    public ServiceResult<Void, AuthRefreshError> refresh(String refreshToken, HttpServletResponse response) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(AuthRefreshError.INVALID_REFRESH_TOKEN);
        }

        Person person = personOptional.get();
        ServiceResult<Void, AuthUpdateStreakError> updateStreakResult = updateStreak(person);
        if (!updateStreakResult.isSuccess()) {
            return ServiceResult.error(AuthRefreshError.STREAK_UPDATE_FAILED);
        }

        jwt.generateAccessTokenCookie(response, person.getUsername());
        jwt.generateRefreshTokenCookie(response, person.getUsername());
        return ServiceResult.success(null);
    }

    public ServiceResult<Void, AuthLogoutError> logout(String refreshToken, HttpServletResponse response) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(AuthLogoutError.INVALID_REFRESH_TOKEN);
        }

        jwt.clearCookies(response);
        return ServiceResult.success(null);
    }

    public ServiceResult<Person, AuthMeError> me(String refreshToken) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(AuthMeError.INVALID_ACCESS_TOKEN);
        }

        Person person = personOptional.get();
        return ServiceResult.success(person);
    }

}
