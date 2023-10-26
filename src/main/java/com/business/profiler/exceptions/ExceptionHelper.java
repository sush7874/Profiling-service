package com.business.profiler.exceptions;

import com.business.profiler.contract.common.Error;
import com.business.profiler.contract.common.ServiceResponse;
import com.business.profiler.contract.common.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExceptionHelper {
    public static final Logger logger = LoggerFactory.getLogger(ExceptionHelper.class);

    @Autowired
    @Qualifier("errorMessageSource")
    MessageSource errorMessageSource;

    public ServiceResponse<Object> prepareErrorResponse(ProfileServiceException pex){
        ServiceResponse<Object> response = new ServiceResponse<>();
        response.setStatus(Status.FAIL);
        List<Error> errors = new ArrayList<>();
        Error error = new Error();
        if(null == pex.getErrorCode()) pex.setErrorCode(ErrorCode.INTERNAL_EXCEPTION);
        error.setErrorCode(pex.getErrorCode().getCode());
        error.setErrorDescription(getErrorDescription(pex));
        errors.add(error);
        response.setErrors(errors);
        return response;
    }

    public ServiceResponse<Object> prepareErrorResponse(Exception pex){
        ServiceResponse<Object> response = new ServiceResponse<>();
        response.setStatus(Status.FAIL);
        List<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setErrorCode("500");
        error.setErrorDescription(pex.getMessage() == null ? "Unknown Error" : pex.getMessage() );
        errors.add(error);
        response.setErrors(errors);
        return response;
    }

    public ServiceResponse<Object> prepareHTTPErrorResponse(Exception pex){
        ServiceResponse<Object> response = new ServiceResponse<>();
        response.setStatus(Status.FAIL);
        List<Error> errors = new ArrayList<>();
        Error error = new Error();
        error.setErrorCode("400");
        error.setErrorDescription(pex.getMessage() == null ? "Unknown Error" : pex.getMessage() );
        errors.add(error);
        response.setErrors(errors);
        return response;
    }


    private String getErrorDescription(ProfileServiceException pex){
        String format = "";
        String errorDesc = "";
        try{
            format = errorMessageSource.getMessage(pex.getErrorCode().getCode(), null, null);
            if(!format.isEmpty()){
                errorDesc = MessageFormat.format(format, (Object[]) pex.getArguments());
            }
        }catch (Exception e){
            logger.error("Exception while getting errorDescription");
        }
        return errorDesc;
    }
}
