package com.business.profiler.exceptions;

import org.springframework.stereotype.Component;

@Component
public class DefaultApiExceptionHandler implements IApiExceptionHandler {

    @Override
    public ProfileServiceException handle(Throwable t){
        if(t instanceof ProfileServiceException){
            return (ProfileServiceException)t;
        } else {
            throw new ProfileServiceException(ErrorCode.INTERNAL_EXCEPTION, t);
        }
    }
}
