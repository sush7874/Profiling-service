package com.business.profiler.mappers;

import com.business.profiler.contract.business.User;
import com.business.profiler.model.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper implements Mapper<User, UserInfo> {

    @Override
    public User reverseMap(UserInfo userInfo){
        User user = new User();
        if(null != userInfo.getUserId()){
            user.setUserId(userInfo.getUserId());
        }
        user.setName(userInfo.getBusinessName());
        user.setAddress(userInfo.getBusinessAddress());
        user.setLegalName(userInfo.getLegalName());
        user.setLegalAddress(userInfo.getLegalAddress());
        user.setPan(userInfo.getPan());
        user.setEmailId(userInfo.getEmailId());
        user.setWebsiteUrl(userInfo.getWebsiteUrl());
        user.setSubscriptionStatus(userInfo.getSubscriptionStatus());

        return user;
    }

    @Override
    public UserInfo map(User user){
        UserInfo userInfo = new UserInfo();
        userInfo.setBusinessName(user.getName());
        if(null!=user.getUserId()){
            userInfo.setUserId(user.getUserId());
        }
        userInfo.setBusinessAddress(user.getAddress());
        userInfo.setLegalName(user.getLegalName());
        userInfo.setLegalAddress(userInfo.getLegalAddress());
        userInfo.setPan(user.getPan());
        userInfo.setEmailId(user.getEmailId());
        userInfo.setWebsiteUrl(user.getWebsiteUrl());
        userInfo.setSubscriptionStatus(user.getSubscriptionStatus());
        return userInfo;
    }
}
