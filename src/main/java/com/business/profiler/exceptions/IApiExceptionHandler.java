package com.business.profiler.exceptions;

public interface IApiExceptionHandler {
    ProfileServiceException handle(Throwable t);
}
