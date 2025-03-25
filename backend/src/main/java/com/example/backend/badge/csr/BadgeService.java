package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.error.BadgeGetByIdError;
import com.example.backend.badge.error.BadgeGetByUserError;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonService;
import com.example.backend.util.JWT;
import com.example.backend.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BadgeService {

    private final JWT jwt;
    private final PersonService personService;
    private final BadgeRepository badgeRepository;

    @Autowired
    public BadgeService(JWT jwt, PersonService personService, BadgeRepository badgeRepository) {
        this.jwt = jwt;
        this.personService = personService;
        this.badgeRepository = badgeRepository;
    }

    public List<Badge> getBadgesByCriteriaType(String criteriaType) {
        return badgeRepository.findByCriteriaType(criteriaType);
    }

    private Person checkAndAwardStreakBadges(Person person) {
        List<Badge> streakBadges = getBadgesByCriteriaType("STREAK");
        for (Badge badge : streakBadges) {
            if (person.getStreak() >= badge.getCriteriaValue() && !person.getBadges().contains(badge)) {
                person.getBadges().add(badge);
            }
        }
        return person;
    }

    private Person checkAndAwardFavoriteBadges(Person person) {
        List<Badge> favoriteBadges = getBadgesByCriteriaType("FAVORITE");
        int favoriteCount = person.getFavoriteCourses().size();
        for (Badge badge : favoriteBadges) {
            if (favoriteCount >= badge.getCriteriaValue() && !person.getBadges().contains(badge)) {
                person.getBadges().add(badge);
            }
        }
        return person;
    }

    // The accessToken must be valid
    private void updateBadges(String accessToken) {
        Person person = jwt.getPersonFromToken(accessToken).get();
        Person streakUpdatedPerson = checkAndAwardStreakBadges(person);
        Person favouriteUpdatedPerson = checkAndAwardFavoriteBadges(streakUpdatedPerson);
        personService.save(favouriteUpdatedPerson);
    }

    public ServiceResult<List<Badge>, Void> getAllBadges() {
        List<Badge> badges = badgeRepository.findAll();
        return ServiceResult.success(badges);
    }

    public ServiceResult<Badge, BadgeGetByIdError> getBadgeById(Integer id) {
        if (id == null || id <= 0) {
            return ServiceResult.error(BadgeGetByIdError.INVALID_ID);
        }
        Optional<Badge> badge = badgeRepository.findById(id);
        if (badge.isEmpty()) {
            return ServiceResult.error(BadgeGetByIdError.BADGE_NOT_FOUND);
        }
        return ServiceResult.success(badge.get());
    }

    public ServiceResult<List<Badge>, BadgeGetByUserError> getUserBadges(String accessToken) {
        Optional<Person> persionOptional = jwt.getPersonFromToken(accessToken);
        if (persionOptional.isEmpty()) {
            return ServiceResult.error(BadgeGetByUserError.INVALID_ACCESS_TOKEN);
        }
        updateBadges(accessToken);
        Person person = persionOptional.get();
        return ServiceResult.success(person.getBadges());
    }
}
