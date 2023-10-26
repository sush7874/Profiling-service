package com.business.profiler.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.model.SubscriptionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class SubscriptionDao {

    private DynamoDBMapper dbMapper;
    @Autowired
    public SubscriptionDao(DynamoDBMapper dynamoDBMapper){
        this.dbMapper = dynamoDBMapper;
    }
    public List<SubscriptionInfo> getSubscriptionDetailsList(String userId){
        Map<String, AttributeValue> eav = new HashMap<String,AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(userId));
        DynamoDBQueryExpression<SubscriptionInfo> queryExpression = new DynamoDBQueryExpression<SubscriptionInfo>()
                .withKeyConditionExpression("userId = :v1").withExpressionAttributeValues(eav);
        return dbMapper.query(SubscriptionInfo.class, queryExpression);
    }

    public SubscriptionInfo createSubscription(SubscriptionInfo subscription){
        dbMapper.save(subscription);
        return subscription;
    }

    public SubscriptionInfo getSubscriptionDetails(String userId, String productId){
        return dbMapper.load(SubscriptionInfo.class, userId, productId);
    }

    public SubscriptionInfo updateSubscriptionStatus(SubscriptionInfo subscriptionInfo){
        SubscriptionInfo sub = dbMapper.load(SubscriptionInfo.class, subscriptionInfo.getUserId(), subscriptionInfo.getProductId());
        if(Objects.nonNull(sub)){
            sub.setSubscriptionStatus(subscriptionInfo.getSubscriptionStatus());
            dbMapper.save(sub);
            return sub;
        }else{
            throw new ProfileServiceException(ErrorCode.NO_SUBSCRIPTIONS_FOUND_FOR_USER);
        }
    }

    public List<SubscriptionInfo> getSubscriptionDetailsListWithProductId(String productId){
        Map<String, AttributeValue> eav = new HashMap<String,AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(productId));
        DynamoDBQueryExpression<SubscriptionInfo> queryExpression = new DynamoDBQueryExpression<SubscriptionInfo>()
                .withKeyConditionExpression("productId = :v1").withExpressionAttributeValues(eav);
        return dbMapper.query(SubscriptionInfo.class, queryExpression);
    }

}
