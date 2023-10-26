package com.business.profiler.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.business.profiler.model.Request;
import com.business.profiler.model.SubscriptionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RequestDao {
    private DynamoDBMapper dbMapper;

    @Autowired
    public RequestDao(DynamoDBMapper dynamoDBMapper){
        this.dbMapper = dynamoDBMapper;
    }

    // Gets details of the product only based on the productId.
    public Request getRequestDetails(String requestId){
       return dbMapper.load(Request.class, requestId);
    }

    // Creates product, assigns unique productId.
    public Request createRequest(Request request){
        dbMapper.save(request);
        return request;
    }

    public List<Request> getRequestsList(String status){
        Map<String, AttributeValue> eav = new HashMap<String,AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(status));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("requestStatus =:v1").withExpressionAttributeValues(eav);
        PaginatedScanList<Request> paginatedScanList = dbMapper.scan(Request.class, scanExpression);

        List<Request> requestList = new ArrayList<>();
        paginatedScanList.stream().forEach(obj -> {
            requestList.add(obj);
        });
        return requestList;
    }

}
