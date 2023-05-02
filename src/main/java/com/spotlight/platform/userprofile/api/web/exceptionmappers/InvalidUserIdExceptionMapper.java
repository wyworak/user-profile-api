package com.spotlight.platform.userprofile.api.web.exceptionmappers;

import com.spotlight.platform.userprofile.api.core.exceptions.InvalidUserIdException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidUserIdExceptionMapper implements ExceptionMapper<InvalidUserIdException> {
    @Override
    public Response toResponse(InvalidUserIdException exception) {
        String errorMessage = "Invalid UserId";
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage)
                .build();
    }
}