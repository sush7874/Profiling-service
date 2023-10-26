package com.business.profiler.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.business.profiler.exceptions.ProfileServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class SubListTypeConverter implements DynamoDBTypeConverter<String, List<SubscriptionInfo>> {

    @Override
    public String convert(List<SubscriptionInfo> objects) {
        //Jackson object mapper
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(objects);
        } catch (JsonProcessingException e) {
            //do something
            throw new ProfileServiceException(e);
        }
    }

    @Override
    public List<SubscriptionInfo> unconvert(String objectssString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<SubscriptionInfo> objects = objectMapper.readValue(objectssString, new TypeReference<>(){});
            return objects;
        } catch (IOException e) {
            throw new ProfileServiceException(e);
        }
    }
}
