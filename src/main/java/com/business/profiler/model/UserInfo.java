package com.business.profiler.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@DynamoDBTable(tableName = "users")
public class UserInfo extends BaseModel{

    @DynamoDBHashKey
    @DynamoDBAutoGeneratedKey
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("businessName")
    @DynamoDBAttribute
    private String businessName;

    @JsonProperty("legalName")
    @DynamoDBAttribute
    private String legalName;

    @JsonProperty("businessAddress")
    @DynamoDBAttribute
    private String businessAddress;

    @JsonProperty("legalAddress")
    @DynamoDBAttribute
    private String legalAddress;

    @JsonProperty("pan")
    @DynamoDBAttribute
    private String pan;

    @JsonProperty("emailId")
    @DynamoDBAttribute
    private String emailId;

    @JsonProperty("websiteUrl")
    @DynamoDBAttribute
    private String websiteUrl;


    @JsonProperty("subscriptionStatus")
    @DynamoDBAttribute
    private String subscriptionStatus;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getBusinessAddress() {
        return businessAddress;
    }

    public void setBusinessAddress(String businessAddress) {
        this.businessAddress = businessAddress;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }


    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }
}