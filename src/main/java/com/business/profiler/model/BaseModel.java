package com.business.profiler.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.business.profiler.util.ObjectMapperFactory;
@JsonSerialize
public class BaseModel {

    @Override
    public String toString(){
        try{
            return ObjectMapperFactory.getMapper().writeValueAsString(this);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return this.toString();
        }
    }
}
