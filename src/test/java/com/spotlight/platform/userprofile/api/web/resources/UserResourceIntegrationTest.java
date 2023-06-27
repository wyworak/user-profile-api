package com.spotlight.platform.userprofile.api.web.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import com.spotlight.platform.userprofile.api.core.json.JsonMapper;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfileFixtures;
import com.spotlight.platform.userprofile.api.web.UserProfileApiApplication;

import com.spotlight.platform.userprofile.api.web.data.UserProfileCommand;
import com.spotlight.platform.userprofile.api.web.data.enums.Command;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Optional;

import ru.vyarus.dropwizard.guice.test.ClientSupport;
import ru.vyarus.dropwizard.guice.test.jupiter.ext.TestDropwizardAppExtension;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@Execution(ExecutionMode.SAME_THREAD)
class UserResourceIntegrationTest {
    @RegisterExtension
    static TestDropwizardAppExtension APP = TestDropwizardAppExtension.forApp(UserProfileApiApplication.class)
            .randomPorts()
            .hooks(builder -> builder.modulesOverride(new AbstractModule() {
                @Provides
                @Singleton
                public UserProfileDao getUserProfileDao() {
                    return mock(UserProfileDao.class);
                }
            }))
            .randomPorts()
            .create();

    @BeforeEach
    void beforeEach(UserProfileDao userProfileDao) {
        reset(userProfileDao);
    }

    @Nested
    @DisplayName("getUserProfile")
    class GetUserProfile {
        private static final String USER_ID_PATH_PARAM = "userId";
        private static final String URL = "/users/{%s}/profile".formatted(USER_ID_PATH_PARAM);

        @Test
        void existingUser_correctObjectIsReturned(ClientSupport client, UserProfileDao userProfileDao) {
            when(userProfileDao.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE));

            var response = client.targetRest().path(URL).resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.USER_ID).request().get();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
            assertThatJson(response.readEntity(UserProfile.class)).isEqualTo(UserProfileFixtures.SERIALIZED_USER_PROFILE);
        }

        @Test
        void nonExistingUser_returns404(ClientSupport client, UserProfileDao userProfileDao) {
            when(userProfileDao.get(any(UserId.class))).thenReturn(Optional.empty());

            var response = client.targetRest().path(URL).resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.USER_ID).request().get();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
        }

        @Test
        void validationFailed_returns400(ClientSupport client) {
            var response = client.targetRest()
                    .path(URL)
                    .resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.INVALID_USER_ID)
                    .request()
                    .get();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        }

        @Test
        void unhandledExceptionOccured_returns500(ClientSupport client, UserProfileDao userProfileDao) {
            when(userProfileDao.get(any(UserId.class))).thenThrow(new RuntimeException("Some unhandled exception"));

            var response = client.targetRest().path(URL).resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.USER_ID).request().get();

            assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @Nested
    @DisplayName("UpdateUserProfile")
    class UpdateUserProfile {
        private static final String USER_ID_PATH_PARAM = "userId";
        private static final String URL = "/users/{%s}/events".formatted(USER_ID_PATH_PARAM);

        @Test
        void validData_updatedUserProfileIsReturned(ClientSupport client, UserProfileDao userProfileDao) throws JsonProcessingException {
            when(userProfileDao.get(any(UserId.class))).thenReturn(Optional.of(UserProfileFixtures.USER_PROFILE_COLLECT));

            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;

            UserProfileCommand userProfileCommand = new UserProfileCommand(userProfile.userId(),
                    Command.COLLECT.name(), userProfile.userProfileProperties());

            String jsonBody = JsonMapper.getInstance().writeValueAsString(userProfileCommand);

            UserProfile updatedUserProfile;
            try (Response response = client.targetRest()
                    .path(URL)
                    .resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.USER_ID)
                    .request()
                    .method("PATCH", Entity.json(jsonBody))) {

                updatedUserProfile = response.readEntity(UserProfile.class);

                assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
            }
            assertThatJson(updatedUserProfile).isEqualTo(UserProfileFixtures.USER_PROFILE_COLLECT);
        }

        @Test
        void differentUserIDs_returns404(ClientSupport client) throws JsonProcessingException {
            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;
            UserProfileCommand userProfileCommand = new UserProfileCommand(userProfile.userId(),
                    Command.COLLECT.name(), userProfile.userProfileProperties());

            String jsonBody = JsonMapper.getInstance().writeValueAsString(userProfileCommand);

            try (Response response = client.targetRest()
                    .path(URL)
                    .resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.INVALID_USER_ID)
                    .request()
                    .method("PATCH", Entity.json(jsonBody))) {
                assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST_400);
            }
        }

        @Test
        void InvalidUpdateCommand_returns500(ClientSupport client) throws JsonProcessingException {
            UserProfile userProfile = UserProfileFixtures.USER_PROFILE_COLLECT;
            UserProfileCommand userProfileCommand = new UserProfileCommand(userProfile.userId(),
                    null, userProfile.userProfileProperties());
            String jsonBody = JsonMapper.getInstance().writeValueAsString(userProfileCommand);

            try (Response response = client.targetRest()
                    .path(URL)
                    .resolveTemplate(USER_ID_PATH_PARAM, UserProfileFixtures.USER_ID)
                    .request()
                    .method("PATCH", Entity.json(jsonBody))) {

                System.out.println(response);

                assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR_500);
            }
        }
    }
}