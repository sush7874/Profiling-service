package com.business.profiler.helper;


import com.business.profiler.contract.business.User;
import com.business.profiler.contract.business.changeRequest;
import com.business.profiler.dao.RequestDao;
import com.business.profiler.dao.UserInfoDao;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.mappers.RequestEntityMapper;
import com.business.profiler.mappers.SubscriptionEntityMapper;
import com.business.profiler.mappers.UserEntityMapper;
import com.business.profiler.model.Request;
import com.business.profiler.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserHelper {
    public static final Logger logger = LoggerFactory.getLogger(UserHelper.class);

    @Autowired
    private UserEntityMapper mapper;

    @Autowired
    private SubscriptionEntityMapper sMapper;

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private RequestDao requestDao;

    @Autowired
    private RequestEntityMapper rMapper;

    public User getUserDetails(User user){
        logger.info("GetProductDetailsApi: Starts");
        UserInfo userInfo = userInfoDao.getUserDetails(user.getUserId());
        if(Objects.nonNull(userInfo)){
            return mapper.reverseMap(userInfo);
        }else {
            throw new ProfileServiceException(ErrorCode.NO_USER_FOUND, user.getUserId());
        }
    }

    public User createUser(User user){
        logger.info("CreateProductApi: Starts");
        UserInfo userInfo = mapper.map(user);

        UserInfo createdUser = userInfoDao.createUser(userInfo);
        if(Objects.nonNull(createdUser)){
            return mapper.reverseMap(createdUser);
        }else {
            throw new ProfileServiceException(ErrorCode.UNABLE_TO_CREATE_USER);
        }

    }

    public User updateUserDetails(User userDetails) {
        UserInfo userInfo = mapper.map(userDetails);
        userInfo = userInfoDao.updateUserDetails(userInfo);
        return mapper.reverseMap(userInfo);
    }

    public changeRequest saveRequest(changeRequest req){
        Request request = rMapper.map(req);
        Request mReq = requestDao.createRequest(request);
        return rMapper.reverseMap(mReq);
    }

    public changeRequest getRequestDetails(String requestId){
        Request mReq = requestDao.getRequestDetails(requestId);
        if(Objects.nonNull(mReq)){
            return rMapper.reverseMap(mReq);
        }else {
            throw new ProfileServiceException(ErrorCode.REQUEST_DOES_NOT_EXIST);
        }
    }

    public List<changeRequest> getChangeRequestList(String status){
        List<Request> changeRequests = requestDao.getRequestsList(status);
        if(changeRequests.isEmpty()){
            throw new ProfileServiceException(ErrorCode.NO_REQUESTS_FOUND_WITH_STATUS, status);
        }
        return rMapper.eGetRequestList(changeRequests);
    }

}
