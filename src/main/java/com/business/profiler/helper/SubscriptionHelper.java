package com.business.profiler.helper;

import com.business.profiler.contract.business.Product;
import com.business.profiler.contract.business.Subscription;
import com.business.profiler.contract.business.User;
import com.business.profiler.dao.SubscriptionDao;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.mappers.RequestEntityMapper;
import com.business.profiler.mappers.SubscriptionEntityMapper;
import com.business.profiler.model.SubscriptionInfo;
import com.business.profiler.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SubscriptionHelper {
    public static final Logger logger = LoggerFactory.getLogger(SubscriptionHelper.class);

    @Autowired
    private SubscriptionEntityMapper sMapper;

    @Autowired
    private SubscriptionDao subDao;

    @Autowired
    private RequestEntityMapper rMapper;

    @Autowired
    private ProductHelper productHelper;

    @Autowired
    private UserHelper userHelper;

    public List<Subscription> getSubscriptionDetailsList(String userId){
        logger.info("Get Subscription List: Starts");

        List<SubscriptionInfo> sub = subDao.getSubscriptionDetailsList(userId);
        if(!sub.isEmpty()){
            List<Subscription> subList =  rMapper.eGetSubList(sub);
            for(Subscription s : subList){
                Product product = productHelper.getProductDetails(s.getProductId());
                s.setProductName(product.getName());
                s.setUserId(null); // no need to return this detail
            }
            return subList;
        }else {
            throw new ProfileServiceException(ErrorCode.NO_SUBSCRIPTIONS_FOUND_FOR_USER, userId);
        }
    }

    public Subscription createSubscription(Subscription product){
        logger.info("Create Subscription: Starts");
        SubscriptionInfo subscriptionInfo = sMapper.map(product);
        // fetch product details and check if product is active/inactive if it is inactive - throw error
        Product getProduct = productHelper.getProductDetails(product.getProductId());
        if(null == getProduct){
            throw new ProfileServiceException(ErrorCode.NO_PRODUCT_FOUND, product.getProductId());
        }
        if(!Constants.ACTIVE.equals(getProduct.getStatus())){
            throw new ProfileServiceException(ErrorCode.PRODUCT_IS_NOT_ACTIVE, product.getProductId());
        }
        SubscriptionInfo subscription = subDao.createSubscription(subscriptionInfo);
        if(Objects.nonNull(subscription)){
            return sMapper.reverseMap(subscription);
        }else {
            throw new ProfileServiceException(ErrorCode.UNABLE_TO_CREATE_SUBSCRIPTION);
        }

    }

    public Subscription getSubscription(String userId, String productId){
        logger.info("Get Subscription Details: Starts");
        SubscriptionInfo subscriptionInfo = subDao.getSubscriptionDetails(userId, productId);
        if(Objects.nonNull(subscriptionInfo)){
            return sMapper.reverseMap(subscriptionInfo);
        }else return null;
    }

    public Subscription updateSubscriptionStatus(Subscription subscription){
        logger.info("Update Subscription Status: Starts");
        try{
            // First check if the update requested for subscription is already in that status. if it is already in the status, throw error
            Subscription currSub = getSubscription(subscription.getUserId(), subscription.getProductId());
            if(null!=currSub && currSub.getStatus().equals(subscription.getStatus())){
                throw new ProfileServiceException(ErrorCode.SUBSCRIPTION_IS_ALREADY_IN_STATUS, subscription.getStatus());
            }


            SubscriptionInfo sub = sMapper.map(subscription);
            SubscriptionInfo updatedSub = subDao.updateSubscriptionStatus(sub);
            // if status is being changed to Inactive, check all the existing subscriptions available with the user and
            // if all subscriptions of the user are inactive, make the user status as inactive too.
            List<Subscription> subList = getSubscriptionDetailsList(subscription.getUserId());
            int count =0;
            for(Subscription s: subList){
                if(Constants.INACTIVE.equals(s.getStatus())){
                    count++;
                }
            }
            if(count==subList.size()){
                User updateUserStatus = new User();
                updateUserStatus.setSubscriptionStatus(Constants.INACTIVE);
                updateUserStatus.setUserId(subscription.getUserId());
                userHelper.updateUserDetails(updateUserStatus);
            }
            return sMapper.reverseMap(updatedSub);
        }catch (ProfileServiceException pex){
            throw pex;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }

    }

    public List<Subscription> getSubscriptionDetailsListWithProductId(String productId){
        logger.info("Get Subscription List: Starts");

        List<SubscriptionInfo> sub = subDao.getSubscriptionDetailsList(productId);
        if(!sub.isEmpty()){
            List<Subscription> subList =  rMapper.eGetSubList(sub);
            for(Subscription s : subList){
                Product product = productHelper.getProductDetails(s.getProductId());
                s.setProductName(product.getName());
            }
            return subList;
        }else {
            return null;
        }
    }
}
