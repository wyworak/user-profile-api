package com.spotlight.platform.userprofile.api.web.exceptionmappers;

import com.spotlight.platform.userprofile.api.core.exceptions.InvalidUpdateCommandException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidUpdateCommandExceptionMapper implements ExceptionMapper<InvalidUpdateCommandException> {
    @Override
    public Response toResponse(InvalidUpdateCommandException exception) {
        String errorMessage = "Invalid update command";
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage)
                .build();
    }
}
