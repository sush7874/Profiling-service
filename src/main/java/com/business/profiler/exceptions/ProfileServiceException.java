package com.business.profiler.exceptions;

import com.business.profiler.util.Constants;

public class ProfileServiceException extends RuntimeException{

    private static final long serialVersionUID = 8323084147015186649L;

    private ErrorCode errorCode;

    private String referenceKey;

    private String serviceName;

    private String[] arguments;

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public ProfileServiceException(ErrorCode errorCode, String message, Throwable cause, boolean enableSuppression,
                                   boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public ProfileServiceException(ErrorCode errorCode, String message, Throwable cause){
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ProfileServiceException(ErrorCode errorCode, Throwable cause){
        super(cause);
        this.errorCode = errorCode;
    }

    public ProfileServiceException(ErrorCode errorCode, String ... arguments){
        super(errorCode.name());
        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public ProfileServiceException(Throwable cause){
        super(cause);
    }

    public String getReferenceKey() {
        return referenceKey == null ? Constants.UNKNOWN : referenceKey;
    }

    public void setReferenceKey(String referenceKey) {
        this.referenceKey = referenceKey;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
