package com.business.profiler.exceptions;

import com.amazonaws.services.s3.internal.eventstreaming.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

@Component
public class ExceptionsLoader {

    @Value("${error.message.properties.location}")
    private String errorPropertiesPath;

    @Bean
    public MessageSource errorMessageSource(){
        ReloadableResourceBundleMessageSource messageSource =
                new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(errorPropertiesPath);
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
