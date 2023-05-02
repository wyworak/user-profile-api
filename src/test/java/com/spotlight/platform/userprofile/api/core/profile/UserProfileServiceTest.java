package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.exceptions.InvalidPropertyException;
import com.spotlight.platform.userprofile.api.core.exceptions.InvalidUpdateCommandException;
import com.spotlight.platform.userprofile.api.core.exceptions.InvalidUserIdException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfileFixtures;
import com.spotlight.platform.userprofile.api.web.data.enums.Command;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserProfileServiceTest {
    private final UserProfileDao userProfileDaoMock = mock(UserProfileDao.class);
    private final UserProfileService userProfileService = new UserProfileService(userProfileDaoMock);

    @Nested
    @DisplayName("get")
    class Get {
        @Test
        void getForExistingUser_returnsUser() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));

            assertThat(userProfileService.get(UserProfileFixtures.USER_ID)).usingRecursiveComparison()
                    .isEqualTo(UserProfileFixtures.USER_PROFILE);
        }

        @Test
        void getForNonExistingUser_throwsException() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userProfileService.get(UserProfileFixtures.USER_ID)).isExactlyInstanceOf(
                    EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("patch")
    class Patch {
        @Test
        void updateWithNewUserProfile_returnsUserUpdated() {
            when(userProfileDaoMock.get(any(UserId.class)))
                    .thenReturn(Optional.empty())
                    .thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;

            assertThat(
                    userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.COLLECT.getType()))
                    .usingRecursiveComparison()
                    .isEqualTo(UserProfileFixtures.USER_PROFILE);
        }

        @Test
        void updateForCollectNewProperty_returnsUserUpdated() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;

            assertThat(
                    userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.COLLECT.getType()))
                    .usingRecursiveComparison()
                    .isEqualTo(UserProfileFixtures.USER_PROFILE);
        }

        @Test
        void updateForCollectExistentProperty_returnsUserUpdated() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE_COLLECT));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;

            assertThat(
                    userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.COLLECT.getType()))
                    .usingRecursiveComparison()
                    .isEqualTo(UserProfileFixtures.USER_PROFILE_COLLECT);
        }

        @Test
        void updateForReplace_returnsUserUpdated() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_REPLACE;

            assertThat(
                    userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.REPLACE.getType()))
                    .usingRecursiveComparison()
                    .isEqualTo(UserProfileFixtures.USER_PROFILE);
        }

        @Test
        void updateForIncrementNewProperty_returnsUserUpdated() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_INCREMENT;

            assertThat(
                    userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.INCREMENT.getType()))
                    .usingRecursiveComparison()
                    .isEqualTo(UserProfileFixtures.USER_PROFILE);
        }

        @Test
        void updateForIncrementExistentProperty_returnsUserUpdated() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE_INCREMENT));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_INCREMENT;

            assertThat(
                    userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.INCREMENT.getType()))
                    .usingRecursiveComparison()
                    .isEqualTo(UserProfileFixtures.USER_PROFILE_INCREMENT);
        }

        @Test
        void updateForIncrementInvalidProperty_throwsException() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_INCREMENT_INVALID;

            assertThatThrownBy(
                    () -> userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.INCREMENT.getType()))
                    .isExactlyInstanceOf(InvalidPropertyException.class);
        }

        @Test
        void updateForInvalidUserId_throwsException() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE;

            assertThatThrownBy(
                    () -> userProfileService.update(UserId.valueOf("InvalidUserId"),
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            Command.INCREMENT.getType()))
                    .isExactlyInstanceOf(InvalidUserIdException.class);
        }

        @Test
        void updateForInvalidUpdateCommand_throwsException() {
            when(userProfileDaoMock.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));
            doNothing().when(userProfileDaoMock).put(any(UserProfile.class));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;

            assertThatThrownBy(
                    () -> userProfileService.update(UserProfileFixtures.USER_ID,
                            userProfile.userId(),
                            userProfile.userProfileProperties(),
                            "InvalidType"))
                    .isExactlyInstanceOf(InvalidUpdateCommandException.class);
        }
    }
}