package com.spotlight.platform.userprofile.api.web.exceptionmappers;

import com.spotlight.platform.userprofile.api.core.exceptions.InvalidPropertyException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidPropertyExceptionMapper implements ExceptionMapper<InvalidPropertyException> {
    @Override
    public Response toResponse(InvalidPropertyException exception) {
        String errorMessage = "Error casting parameter";
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage)
                .build();
    }
}