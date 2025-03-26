package com.example.backend.friend.csr;

import com.example.backend.friend.FriendDTO;
import com.example.backend.friend.FriendResponseDTO;
import com.example.backend.friend.error.FriendAddError;
import com.example.backend.friend.error.FriendGetAllError;
import com.example.backend.friend.error.FriendRemoveError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FriendService {
    
    private final JWT jwt;
    private final PersonService personService;
    
    public FriendService(JWT jwt, PersonService personService) {
        this.jwt = jwt;
        this.personService = personService;
    }
    
    public ServiceResult<List<FriendResponseDTO>, FriendGetAllError> getAllFriends(String accessToken) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(FriendGetAllError.INVALID_ACCESS_TOKEN);
        }
        
        Person person = personOptional.get();
        List<Person> friends = person.getFriends();
        
        if (friends == null || friends.isEmpty()) {
            return ServiceResult.success(new ArrayList<>());
        }
        
        List<FriendResponseDTO> friendDTOs = friends.stream()
                .map(friend -> new FriendResponseDTO(
                        friend.getUsername(),
                        friend.getFirstName(),
                        friend.getLastName(),
                        friend.getEmail(),
                        friend.getAvatarLink()))
                .toList();
        
        return ServiceResult.success(friendDTOs);
    }
    
    public ServiceResult<Void, FriendAddError> addFriend(String accessToken, FriendDTO friendDTO) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(FriendAddError.INVALID_ACCESS_TOKEN);
        }
        
        Person person = personOptional.get();
        
        // Cannot add yourself as a friend
        if (person.getId().equals(friendDTO.getPersonId())) {
            return ServiceResult.error(FriendAddError.CANNOT_ADD_SELF);
        }
        
        Optional<Person> friendOptional = personService.findById(friendDTO.getPersonId());
        if (friendOptional.isEmpty()) {
            return ServiceResult.error(FriendAddError.PERSON_NOT_FOUND);
        }
        
        Person friend = friendOptional.get();
        
        // Check if already friends
        if (person.getFriends().contains(friend)) {
            return ServiceResult.error(FriendAddError.ALREADY_FRIENDS);
        }
        
        // Add friend to user's friend list
        person.getFriends().add(friend);
        Optional<Person> updatedPerson = personService.save(person);
        
        if (updatedPerson.isEmpty()) {
            return ServiceResult.error(FriendAddError.FRIEND_ADD_FAILED);
        }
        
        return ServiceResult.success(null);
    }
    
    public ServiceResult<Void, FriendRemoveError> removeFriend(String accessToken, Long friendId) {
        Optional<Person> personOptional = jwt.getPersonFromToken(accessToken);
        if (personOptional.isEmpty()) {
            return ServiceResult.error(FriendRemoveError.INVALID_ACCESS_TOKEN);
        }
        
        Person person = personOptional.get();
        
        Optional<Person> friendOptional = personService.findById(friendId);
        if (friendOptional.isEmpty()) {
            return ServiceResult.error(FriendRemoveError.PERSON_NOT_FOUND);
        }
        
        Person friend = friendOptional.get();
        
        // Check if they are friends
        if (!person.getFriends().contains(friend)) {
            return ServiceResult.error(FriendRemoveError.NOT_FRIENDS);
        }
        
        // Remove friend from user's friend list
        person.getFriends().remove(friend);
        Optional<Person> updatedPerson = personService.save(person);
        
        if (updatedPerson.isEmpty()) {
            return ServiceResult.error(FriendRemoveError.FRIEND_REMOVE_FAILED);
        }
        
        return ServiceResult.success(null);
    }
}