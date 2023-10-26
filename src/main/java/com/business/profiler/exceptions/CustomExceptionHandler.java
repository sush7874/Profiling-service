package com.business.profiler.exceptions;

import com.business.profiler.contract.common.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    public static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @Autowired
    ExceptionHelper exceptionHelper;

    @ExceptionHandler(ProfileServiceException.class)
    public final ResponseEntity<Object> handleProfileServiceException(ProfileServiceException pex, WebRequest request){
        logger.error("Exception in handleProfileServiceException for {}", request, pex);

        ServiceResponse<Object> errorResponse  = exceptionHelper.prepareErrorResponse(pex);

        return ResponseEntity.status(getHTTPStatusCode(pex)).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request){
        logger.error("Exception in handleGenericException for {}", request, ex);

        ServiceResponse<Object> errorResponse  = exceptionHelper.prepareErrorResponse(ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request){
        logger.error("Exception in handleExceptionInternal for {}", request, ex);

        ServiceResponse<Object> errorResponse  = exceptionHelper.prepareHTTPErrorResponse(ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private HttpStatus getHTTPStatusCode(ProfileServiceException pex){
        if(pex.getErrorCode().getCode().contains("403")){
            return HttpStatus.FORBIDDEN;
        }else if(pex.getErrorCode().getCode().contains("400")){
            return HttpStatus.BAD_REQUEST;
        }else{
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

}
