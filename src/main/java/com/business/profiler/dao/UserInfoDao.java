package com.business.profiler.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Repository
public class UserInfoDao {
    private DynamoDBMapper dbMapper;

    @Autowired
    public UserInfoDao(DynamoDBMapper dynamoDBMapper){
        this.dbMapper = dynamoDBMapper;
    }

    public UserInfo getUserDetails(String userId){
        return dbMapper.load(UserInfo.class, userId);
    }

    public UserInfo createUser(UserInfo user){
        dbMapper.save(user);
        return user;
    }

    // Update is allowed only for name, address, legal address, url, email.
    // update is not allowed for pan, legal name, userId,
    public UserInfo updateUserDetails(UserInfo userInfo){
        UserInfo toUpdate = dbMapper.load(UserInfo.class, userInfo.getUserId());
        if(Objects.nonNull(toUpdate)){
            if(!StringUtils.isEmpty(userInfo.getBusinessAddress())) toUpdate.setBusinessAddress(userInfo.getBusinessAddress());
            if(!StringUtils.isEmpty(userInfo.getBusinessName())) toUpdate.setBusinessName(userInfo.getBusinessName());
            if(!StringUtils.isEmpty(userInfo.getLegalAddress())) toUpdate.setLegalAddress(userInfo.getLegalAddress());
            if(!StringUtils.isEmpty(userInfo.getWebsiteUrl())) toUpdate.setWebsiteUrl(userInfo.getWebsiteUrl());
            if(!StringUtils.isEmpty(userInfo.getEmailId())) toUpdate.setEmailId(userInfo.getEmailId());
            if(!StringUtils.isEmpty(userInfo.getSubscriptionStatus())) toUpdate.setSubscriptionStatus(userInfo.getSubscriptionStatus());

            dbMapper.save(toUpdate);
            return toUpdate;
        }else {
            throw new ProfileServiceException(ErrorCode.NO_USER_FOUND);
        }
    }

}
