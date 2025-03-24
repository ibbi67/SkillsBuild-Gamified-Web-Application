package com.example.backend.profile.csr;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.profile.ProfileDTO;
import com.example.backend.profile.error.ProfileUpdateError;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;

@Service
public class ProfileService {

    private final JWT jwt;
    private final PersonService personService;

    public ProfileService(JWT jwt, PersonService personService) {
        this.jwt = jwt;
        this.personService = personService;
    }

    public ServiceResult<Void, ProfileUpdateError> update(String accessToken, ProfileDTO profileDTO) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(ProfileUpdateError.INVALID_ACCESS_TOKEN);
        }
        Person person = personOptional.get();
        personOptional = personService.updatePerson(person, profileDTO);
        if (personOptional.isPresent()) {
            return ServiceResult.success(null);
        }
        return ServiceResult.error(ProfileUpdateError.PROFILE_UPDATE_FAILED);
    }
}
