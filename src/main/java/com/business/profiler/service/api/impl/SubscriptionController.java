package com.business.profiler.service.api.impl;

import com.business.profiler.contract.business.Subscription;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.contract.common.ServiceResponse;
import com.business.profiler.contract.common.Status;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.helper.SubscriptionHelper;
import com.business.profiler.service.api.SubscriptionResources;
import com.business.profiler.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Transactional
public class SubscriptionController implements SubscriptionResources {
    public static final Logger logger = LoggerFactory.getLogger(SubscriptionController.class);

    @Autowired
    private SubscriptionHelper sHelper;
    @Override
    public ServiceResponse<List<Subscription>> getSubscriptionDetails(String userId) {
        logger.info("Inside Get subscription Details for the User method");
        try{
            if(StringUtils.isEmpty(userId)){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            List<Subscription> sList = sHelper.getSubscriptionDetailsList(userId);
            ServiceResponse<List<Subscription>> response = new ServiceResponse<>();
            response.setResult(sList);
            response.setStatus(Status.SUCCESS);
            return response;

        }catch (ProfileServiceException pex){
            throw pex;
        }catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }
    @Override
    public ServiceResponse<Subscription> addSubscription(ServiceRequest<Subscription> input) {
        logger.info("Inside add subscription method");
        try{
            if(Objects.isNull(input) || Objects.isNull(input.getPayload())){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }

            Subscription inReq = input.getPayload();
            // if the subscription exists, will not create another subscription
            Subscription exists = sHelper.getSubscription(inReq.getUserId(), inReq.getProductId());
            if(Objects.isNull(exists)){
                if(null==inReq.getStatus() || inReq.getStatus().isEmpty()){
                    inReq.setStatus(Constants.ACTIVE);
                }
                exists = sHelper.createSubscription(inReq);
            }else{
                throw new ProfileServiceException(ErrorCode.SUBSCRIPTION_ALREADY_EXISTS, "Subscription exists");
            }
            ServiceResponse<Subscription> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(exists);
            return response;
        }catch (ProfileServiceException pe){
            throw pe;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }

    @Override
    public ServiceResponse<Subscription> updateSubscriptionStatus(ServiceRequest<Subscription> input) {
        logger.info("Inside updateSubscriptionStatus API");
        try{
            if(Objects.isNull(input) || Objects.isNull(input.getPayload())){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            Subscription sub = input.getPayload();
            if(StringUtils.isEmpty(sub.getStatus())){
                throw new ProfileServiceException(ErrorCode.SUBSCRIPTION_STATUS_IS_NULL_OR_EMPTY);
            }
            sub = sHelper.updateSubscriptionStatus(sub);
            ServiceResponse<Subscription> response = new ServiceResponse<>();
            response.setResult(sub);
            response.setStatus(Status.SUCCESS);
            return response;
        }catch (ProfileServiceException pe){
            throw pe;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }
}
