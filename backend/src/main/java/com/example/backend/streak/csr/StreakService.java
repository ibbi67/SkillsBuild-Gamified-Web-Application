package com.example.backend.streak.csr;

import com.example.backend.person.Person;
import com.example.backend.streak.error.StreakGetError;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StreakService {

    private final JWT jwt;

    public StreakService(JWT jwt) {
        this.jwt = jwt;
    }

    public ServiceResult<Integer, StreakGetError> getStreak(String refreshToken) {
        Optional<Person> personOptional = jwt.getPersonFromToken(refreshToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(StreakGetError.INVALID_ACCESS_TOKEN);
        }

        Person person = personOptional.get();
        return ServiceResult.success(person.getStreak());
    }
}
