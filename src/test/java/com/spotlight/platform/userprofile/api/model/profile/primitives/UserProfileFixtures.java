package com.spotlight.platform.userprofile.api.model.profile.primitives;

import com.spotlight.platform.helpers.FixtureHelpers;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;

import java.time.Instant;
import java.util.Map;

public class UserProfileFixtures {
    public static final UserId USER_ID = UserId.valueOf("existing-user-id");
    public static final UserId NON_EXISTING_USER_ID = UserId.valueOf("non-existing-user-id");
    public static final UserId INVALID_USER_ID = UserId.valueOf("invalid-user-id-%");

    public static final Instant LAST_UPDATE_TIMESTAMP = Instant.parse("2021-06-01T09:16:36.123Z");

    public static final UserProfile USER_PROFILE = new UserProfile(USER_ID, LAST_UPDATE_TIMESTAMP,
            Map.of(UserProfilePropertyName.valueOf("property1"), UserProfilePropertyValue.valueOf("property1Value")));

    public static final UserProfile USER_PROFILE_COLLECT = new UserProfile(USER_ID, LAST_UPDATE_TIMESTAMP,
            Map.of(
                    UserProfilePropertyName.valueOf("inventory"), UserProfilePropertyValue.valueOf("[\"sword1\", \"sword2\", \"shield1\"]"),
                    UserProfilePropertyName.valueOf("tools"), UserProfilePropertyValue.valueOf("[\"tool1\", \"tool2\"]")));

    public static final UserProfile USER_PROFILE_INCREMENT = new UserProfile(USER_ID, LAST_UPDATE_TIMESTAMP,
            Map.of(
                    UserProfilePropertyName.valueOf("battleFought"), UserProfilePropertyValue.valueOf("10"),
                    UserProfilePropertyName.valueOf("questsNotCompleted"), UserProfilePropertyValue.valueOf("-1")));

    public static final UserProfile USER_PROFILE_INCREMENT_INVALID = new UserProfile(USER_ID, LAST_UPDATE_TIMESTAMP,
            Map.of(UserProfilePropertyName.valueOf("property"), UserProfilePropertyValue.valueOf("A")));

    public static final UserProfile USER_PROFILE_REPLACE = new UserProfile(USER_ID, LAST_UPDATE_TIMESTAMP,
            Map.of(
                    UserProfilePropertyName.valueOf("currentGold"), UserProfilePropertyValue.valueOf("500"),
                    UserProfilePropertyName.valueOf("currentGems"), UserProfilePropertyValue.valueOf("800")));

    public static final String SERIALIZED_USER_PROFILE = FixtureHelpers.fixture("/fixtures/model/profile/userProfile.json");
    public static final String SERIALIZED_USER_PROFILE_COLLECT = FixtureHelpers.fixture("/fixtures/model/profile/userProfileCollect.json");
    public static final String SERIALIZED_USER_PROFILE_INCREMENT = FixtureHelpers.fixture("/fixtures/model/profile/userProfileIncrement.json");
    public static final String SERIALIZED_USER_PROFILE_REPLACE = FixtureHelpers.fixture("/fixtures/model/profile/userProfileReplace.json");
}
