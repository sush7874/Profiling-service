package com.business.profiler.mappers;

import com.business.profiler.model.SubscriptionInfo;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionEntityMapper implements Mapper<com.business.profiler.contract.business.Subscription, SubscriptionInfo> {

    @Override
    public com.business.profiler.contract.business.Subscription reverseMap(SubscriptionInfo subscription){
        com.business.profiler.contract.business.Subscription sub = new com.business.profiler.contract.business.Subscription();
        sub.setProductId(subscription.getProductId());
        sub.setStatus(subscription.getSubscriptionStatus());
        sub.setUserId(subscription.getUserId());
        return sub;
    }

    @Override
    public SubscriptionInfo map(com.business.profiler.contract.business.Subscription sub){

        SubscriptionInfo subscription = new SubscriptionInfo();
        subscription.setProductId(sub.getProductId());
        subscription.setSubscriptionStatus(sub.getStatus());
        subscription.setUserId(sub.getUserId());

        return subscription;
    }
}
