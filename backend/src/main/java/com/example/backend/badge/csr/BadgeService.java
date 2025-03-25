package com.example.backend.badge.csr;

import com.example.backend.badge.Badge;
import com.example.backend.badge.BadgeDTO;
import com.example.backend.person.Person;
import com.example.backend.person.csr.PersonRepository;
import com.example.backend.badge.error.BadgeCreateError;
import com.example.backend.badge.error.BadgeGetError;
import com.example.backend.badge.error.BadgeAwardError;
import com.example.backend.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.logging.Level;

@Service
public class BadgeService {
    private static final Logger logger = Logger.getLogger(BadgeService.class.getName());

    private final BadgeRepository badgeRepository;
    private final PersonRepository personRepository;

    @Autowired
    public BadgeService(BadgeRepository badgeRepository, PersonRepository personRepository) {
        this.badgeRepository = badgeRepository;
        this.personRepository = personRepository;
    }

    public void initializeDefaultBadges() {
        if (badgeRepository.count() == 0) {
            createBadge(new BadgeDTO("First Favorite", "Added your first course to favorites", 
                "/badges/favorite-1.png", "FAVORITE", 1));
            createBadge(new BadgeDTO("Favorites Collector", "Added 2 courses to favorites",
                "/badges/favorite-2.png", "FAVORITE", 2));
            createBadge(new BadgeDTO("Favorites Enthusiast", "Added 5 courses to favorites",
                "/badges/favorite-5.png", "FAVORITE", 5));
            createBadge(new BadgeDTO("Streak Starter", "Maintained a 3-day streak",
                "/badges/streak-3.png", "STREAK", 3));
            createBadge(new BadgeDTO("Streak Master", "Maintained a 7-day streak",
                "/badges/streak-7.png", "STREAK", 7));
        }
    }

    public ServiceResult<Badge, BadgeCreateError> createBadge(BadgeDTO badgeDTO) {
        try {
            if (badgeDTO == null) {
                return ServiceResult.error(BadgeCreateError.BADGE_CREATION_FAILED);
            }
            
            Badge badge = new Badge(
                badgeDTO.getName(),
                badgeDTO.getDescription(),
                badgeDTO.getImageUrl(),
                badgeDTO.getCriteriaType(),
                badgeDTO.getCriteriaValue()
            );
            return ServiceResult.success(badgeRepository.save(badge));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to create badge", e);
            return ServiceResult.error(BadgeCreateError.BADGE_CREATION_FAILED);
        }
    }

    public ServiceResult<List<Badge>, BadgeGetError> getAllBadges() {
        try {
            List<Badge> badges = badgeRepository.findAll();
            if (badges.isEmpty()) {
                return ServiceResult.error(BadgeGetError.BADGE_NOT_FOUND);
            }
            return ServiceResult.success(badges);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get all badges", e);
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }
    }

    public ServiceResult<Badge, BadgeGetError> getBadgeById(Integer id) {
        if (id == null) {
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }

        try {
            Optional<Badge> badge = badgeRepository.findById(id);
            if (badge.isEmpty()) {
                return ServiceResult.error(BadgeGetError.BADGE_NOT_FOUND);
            }
            return ServiceResult.success(badge.get());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get badge by ID: " + id, e);
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }
    }

    public ServiceResult<Badge, BadgeGetError> getBadgeByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }

        try {
            Optional<Badge> badgeOptional = badgeRepository.findByName(name);
            if (badgeOptional.isEmpty()) {
                return ServiceResult.error(BadgeGetError.BADGE_NOT_FOUND);
            }
            return ServiceResult.success(badgeOptional.get());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get badge by name: " + name, e);
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }
    }

    public ServiceResult<List<Badge>, BadgeGetError> getBadgesByCriteriaType(String criteriaType) {
        if (criteriaType == null || criteriaType.trim().isEmpty()) {
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }

        try {
            List<Badge> badges = badgeRepository.findByCriteriaType(criteriaType);
            if (badges.isEmpty()) {
                return ServiceResult.error(BadgeGetError.BADGE_NOT_FOUND);
            }
            return ServiceResult.success(badges);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get badges by criteria type: " + criteriaType, e);
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }
    }

    @Transactional
    public ServiceResult<Void, BadgeAwardError> awardBadgeToUser(Integer userId, Integer badgeId) {
        if (userId == null || badgeId == null) {
            return ServiceResult.error(BadgeAwardError.BADGE_AWARD_FAILED);
        }

        try {
            Optional<Person> personOpt = personRepository.findById(userId.longValue());
            if (personOpt.isEmpty()) {
                return ServiceResult.error(BadgeAwardError.USER_NOT_FOUND);
            }

            Optional<Badge> badgeOpt = badgeRepository.findById(badgeId);
            if (badgeOpt.isEmpty()) {
                return ServiceResult.error(BadgeAwardError.BADGE_NOT_FOUND);
            }

            Person person = personOpt.get();
            Badge badge = badgeOpt.get();

            person.getBadges().add(badge);
            badge.addPerson(person);

            personRepository.save(person);
            badgeRepository.save(badge);

            return ServiceResult.success(null);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to award badge (userId: " + userId + ", badgeId: " + badgeId + ")", e);
            return ServiceResult.error(BadgeAwardError.BADGE_AWARD_FAILED);
        }
    }

    @Transactional
    public void checkAndAwardFavoriteBadges(Person person) {
        if (person == null || person.getFavoriteCourses() == null) {
            return;
        }

        try {
            int favoriteCount = person.getFavoriteCourses().size();
            if (favoriteCount == 0) {
                return;
            }
            
            List<Badge> favoriteBadges = badgeRepository.findByCriteriaType("FAVORITE");
            
            boolean badgeAwarded = false;
            for (Badge badge : favoriteBadges) {
                if (favoriteCount >= badge.getCriteriaValue() && !person.getBadges().contains(badge)) {
                    person.getBadges().add(badge);
                    badge.addPerson(person);
                    badgeAwarded = true;
                }
            }
            
            if (badgeAwarded) {
                personRepository.save(person);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in checkAndAwardFavoriteBadges for person: " + person.getId(), e);
            // Don't propagate the exception to avoid breaking the calling method
        }
    }

    @Transactional
    public void checkAndAwardStreakBadges(Person person) {
        if (person == null) {
            return;
        }

        try {
            List<Badge> streakBadges = badgeRepository.findByCriteriaType("STREAK");
            boolean badgeAwarded = false;

            for (Badge badge : streakBadges) {
                if (person.getStreak() >= badge.getCriteriaValue() && !person.getBadges().contains(badge)) {
                    person.getBadges().add(badge);
                    badge.addPerson(person);
                    badgeAwarded = true;
                }
            }

            if (badgeAwarded) {
                personRepository.save(person);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in checkAndAwardStreakBadges for person: " + person.getId(), e);
            // Don't propagate the exception to avoid breaking the calling method
        }
    }

    public ServiceResult<Set<Badge>, BadgeGetError> getUserBadges(Integer userId) {
        if (userId == null) {
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }

        try {
            Optional<Person> personOpt = personRepository.findById(userId.longValue());
            if (personOpt.isEmpty()) {
                return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
            }

            Person person = personOpt.get();
            return ServiceResult.success(person.getBadges());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to get user badges for userId: " + userId, e);
            return ServiceResult.error(BadgeGetError.GET_BADGE_FAILED);
        }
    }
    
    public Optional<Badge> findBadgeByCriteriaTypeAndValue(String criteriaType, Integer criteriaValue) {
        try {
            if (criteriaType == null || criteriaValue == null) {
                return Optional.empty();
            }
            return badgeRepository.findByCriteriaTypeAndCriteriaValue(criteriaType, criteriaValue);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to find badge by criteria type: " + criteriaType + 
                       " and value: " + criteriaValue, e);
            return Optional.empty();
        }
    }
}