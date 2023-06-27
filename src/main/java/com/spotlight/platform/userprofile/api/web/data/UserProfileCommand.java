package com.spotlight.platform.userprofile.api.web.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;

import javax.validation.constraints.AssertTrue;
import java.util.Map;

public record UserProfileCommand(@JsonProperty UserId userId,
                                 @JsonProperty String type,
                                 @JsonProperty Map<UserProfilePropertyName, UserProfilePropertyValue> properties) {

    @AssertTrue(message = "The userId must be the same as the path parameter")
    public boolean isUserIdValid(UserId pathUserId) {
        return userId != null && userId.equals(pathUserId);
    }
}