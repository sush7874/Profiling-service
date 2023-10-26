package com.business.profiler.external;

import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class InvokeExtAPI {
    public static final Logger logger = LoggerFactory.getLogger(InvokeExtAPI.class);

    @Autowired
    private RestTemplate restTemplate;


   @HystrixCommand(fallbackMethod="externalTimeoutHandler", commandKey = "extnProdCmd")
   @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    public String invokeExternalApi(String url, String request){
        HttpEntity<String> httpEntity = getHttpEntity(request);
        ResponseEntity<String> httpResponse;
        try{
            httpResponse = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

            if(httpResponse.getStatusCode().is2xxSuccessful()){
                return httpResponse.getBody();
            }else {
                throw new ProfileServiceException(ErrorCode.EXTERNAL_INTEGRATION_ERROR, httpResponse.getBody());
            }
        }catch (HttpServerErrorException e){
            logger.error("HttpServerErrorException. url: "+ url, e);
            throw new ProfileServiceException(ErrorCode.HTTP_SERVER_EXCEPTION, e.getResponseBodyAsString());
        }catch (RestClientException re){
            logger.error("RestClientException. url: " + url, re);
            throw new ProfileServiceException(ErrorCode.ACKNOWLEDGEMENT_NOT_RECEIVED, url, re);
        }catch (Exception e){
            throw new ProfileServiceException(e);
        }

    }

    public String externalTimeoutHandler(String url){
       logger.info("Executing fallback for url {} ", url);
       return null;
    }

    private HttpEntity<String> getHttpEntity(String request) {
        List<MediaType> accepts = new ArrayList<>();
        HttpHeaders headers = new HttpHeaders();
        accepts.add(MediaType.APPLICATION_JSON);
        headers.setAccept(accepts);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }

}
