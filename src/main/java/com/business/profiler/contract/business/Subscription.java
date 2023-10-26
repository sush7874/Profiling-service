package com.business.profiler.contract.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.business.profiler.model.BaseModel;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subscription extends BaseModel {

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("subscriptionStatus")
    private String status;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("productName")
    private String productName;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductName() { return productName; }

    public void setProductName(String productName) { this.productName = productName; }
}
