package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.exceptions.InvalidPropertyException;
import com.spotlight.platform.userprofile.api.core.exceptions.InvalidUpdateCommandException;
import com.spotlight.platform.userprofile.api.core.exceptions.InvalidUserIdException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;
import com.spotlight.platform.userprofile.api.web.data.enums.Command;

import javax.inject.Inject;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserProfileService {
    private static final String REPLACE_ARRAY_REGEX = "[\\[\\]\\s]";
    private static final String SPLIT_ARRAY_REGEX = ",";
    private final UserProfileDao userProfileDao;

    @Inject
    public UserProfileService(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    public UserProfile get(UserId userId) {
        return userProfileDao.get(userId).orElseThrow(EntityNotFoundException::new);
    }

    public UserProfile update(UserId userId,
                              UserId commandUserId,
                              Map<UserProfilePropertyName,
                                      UserProfilePropertyValue> properties,
                              String type) {
        validateUserId(userId, commandUserId);
        Command command = getUpdateCommand(type);
        Optional<UserProfile> profile = userProfileDao.get(userId);

        if (profile.isEmpty()) {
            persistUserProfile(userId, properties);
        } else {
            switch (command) {
                case COLLECT -> addValueToList(userId, properties);
                case INCREMENT -> incrementValue(userId, properties);
                case REPLACE -> replaceValue(userId, properties);
            }
        }

        return get(userId);
    }

    private void validateUserId(UserId userId, UserId commandUserId) {
        if (!userId.equals(commandUserId)) {
            throw new InvalidUserIdException();
        }
    }

    private Command getUpdateCommand(String type) {
        try {
            return Command.fromType(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidUpdateCommandException(e);
        }
    }

    private void persistUserProfile(UserId userId, Map<UserProfilePropertyName, UserProfilePropertyValue> properties) {
        UserProfile userProfile = new UserProfile(userId, Instant.now(Clock.systemUTC()), properties);
        userProfileDao.put(userProfile);
    }

    private void addValueToList(UserId userId, Map<UserProfilePropertyName, UserProfilePropertyValue> properties) {
        Map<UserProfilePropertyName, UserProfilePropertyValue> currentProperties = loadProperties(userId);

        for (UserProfilePropertyName propertyName : properties.keySet()) {
            UserProfilePropertyValue propertyValue = properties.get(propertyName);

            if (currentProperties.containsKey(propertyName)) {
                UserProfilePropertyValue currentPropertyValue = currentProperties.get(propertyName);

                List<String> contentList = new ArrayList<>(getContentListFromProperty(currentPropertyValue));
                contentList.addAll(getContentListFromProperty(propertyValue));

                currentProperties.put(propertyName, UserProfilePropertyValue.valueOf(contentList));

            } else {
                currentProperties.put(propertyName, UserProfilePropertyValue.valueOf(propertyValue.getValue()));
            }
        }
        persistUserProfile(userId, currentProperties);
    }

    private void incrementValue(UserId userId, Map<UserProfilePropertyName, UserProfilePropertyValue> properties) {
        Map<UserProfilePropertyName, UserProfilePropertyValue> currentProperties = loadProperties(userId);

        for (UserProfilePropertyName propertyName : properties.keySet()) {
            try {
                int propertyValue = Integer.parseInt(String.valueOf(properties.get(propertyName).getValue()));

                if (currentProperties.containsKey(propertyName)) {
                    String value = String.valueOf(currentProperties.get(propertyName).getValue());

                    int currentPropertyValue = Integer.parseInt(value);

                    currentPropertyValue += propertyValue;

                    currentProperties.put(propertyName, UserProfilePropertyValue.valueOf(currentPropertyValue));
                } else {
                    currentProperties.put(propertyName, UserProfilePropertyValue.valueOf(propertyValue));
                }
            } catch (Exception e) {
                throw new InvalidPropertyException(e);
            }
        }
        persistUserProfile(userId, currentProperties);
    }

    private void replaceValue(UserId userId, Map<UserProfilePropertyName, UserProfilePropertyValue> properties) {
        Map<UserProfilePropertyName, UserProfilePropertyValue> currentProperties = loadProperties(userId);

        for (UserProfilePropertyName newPropertyName : properties.keySet()) {
            currentProperties.put(newPropertyName, UserProfilePropertyValue.valueOf(properties.get(newPropertyName)));
        }

        persistUserProfile(userId, currentProperties);
    }

    private Map<UserProfilePropertyName, UserProfilePropertyValue> loadProperties(UserId userId) {
        UserProfile userProfile = get(userId);
        return new HashMap<>(userProfile.userProfileProperties());
    }

    private List<String> getContentListFromProperty(UserProfilePropertyValue propertyValue) {
        return Arrays.stream(Pattern.compile(REPLACE_ARRAY_REGEX)
                        .matcher(propertyValue.getValue().toString())
                        .replaceAll("")
                        .split(SPLIT_ARRAY_REGEX))
                .toList();
    }
}
