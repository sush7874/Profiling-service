package com.business.profiler.mappers;

import com.business.profiler.contract.business.changeRequest;
import com.business.profiler.contract.business.User;
import com.business.profiler.model.Request;
import com.business.profiler.model.SubscriptionInfo;
import com.business.profiler.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RequestEntityMapper implements Mapper<changeRequest, com.business.profiler.model.Request> {

    @Autowired
    private SubscriptionEntityMapper sMapper;

    @Autowired
    private UserEntityMapper mapper;
    @Override
    public com.business.profiler.model.Request map(changeRequest entity) {
        com.business.profiler.model.Request mReq = new com.business.profiler.model.Request();
        mReq.setRequestType(entity.getRequestType());
        mReq.setRequestId(entity.getRequestId());
        mReq.setSubscriptionList(mGetSubList(entity.getUserDetails().getSubscriptionList()));
        UserInfo userInfo = mapper.map(entity.getUserDetails());
        mReq.setUserDetails(userInfo);
        mReq.setRequestStatus(entity.getRequestStatus());
        return mReq;
    }

    @Override
    public changeRequest reverseMap(com.business.profiler.model.Request model) {

        changeRequest eReq = new changeRequest();
        eReq.setRequestId(model.getRequestId());
        eReq.setRequestType(model.getRequestType());
        eReq.setRequestStatus(model.getRequestStatus());
        User user = mapper.reverseMap(model.getUserDetails());
        user.setSubscriptionList(eGetSubList(model.getSubscriptionList()));

        eReq.setUserDetails(user);
        return eReq;
    }

    public List<SubscriptionInfo> mGetSubList(List<com.business.profiler.contract.business.Subscription> subList){
        List<SubscriptionInfo> mSubList = new ArrayList<>();
        for (com.business.profiler.contract.business.Subscription s : subList){
            SubscriptionInfo mSub = sMapper.map(s);
            mSubList.add(mSub);
        }
        return mSubList;
    }

    public List<com.business.profiler.contract.business.Subscription> eGetSubList(List<SubscriptionInfo> subList){
        List<com.business.profiler.contract.business.Subscription> eSubList = new ArrayList<>();
        for (SubscriptionInfo s : subList){
            com.business.profiler.contract.business.Subscription mSub = sMapper.reverseMap(s);
            eSubList.add(mSub);
        }
        return eSubList;
    }

    public List<Request> mgetRequestList(List<changeRequest> reqList){
        List<Request> mReqList = new ArrayList<>();
        if(reqList.isEmpty()) return mReqList;
        for(changeRequest r : reqList){
            Request req = map(r);
            mReqList.add(req);
        }
        return mReqList;
    }

    public List<changeRequest> eGetRequestList(List<Request> reqList){
        List<changeRequest> eReqList = new ArrayList<>();
        if(reqList.isEmpty()) return eReqList;
        for(Request r : reqList){
            changeRequest changeRequest = reverseMap(r);
            eReqList.add(changeRequest);
        }
        return eReqList;

    }
}
