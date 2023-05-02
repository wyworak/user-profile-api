package com.spotlight.platform.userprofile.api.web.data;

import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfileFixtures;
import com.spotlight.platform.userprofile.api.web.data.enums.Command;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserProfileCommandTest {

    @Test
    void serialization_WorksAsExpected() {
        UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;

        UserProfileCommand userProfileCommand = new UserProfileCommand(userProfile.userId(),
                Command.COLLECT.getType(), userProfile.userProfileProperties());

        assertEquals(userProfile.userId(), userProfileCommand.userId());
        assertEquals(Command.COLLECT.getType(), userProfileCommand.type());
        assertEquals(userProfile.userProfileProperties(), userProfileCommand.properties());
    }
}