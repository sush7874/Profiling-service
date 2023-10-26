package com.business.profiler.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.model.ProductInfo;
import com.business.profiler.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;

@Repository
public class ProductDao {
    private DynamoDBMapper dbMapper;

    @Autowired
    public ProductDao(DynamoDBMapper dynamoDBMapper){
        this.dbMapper = dynamoDBMapper;
    }

    // Gets details of the product only based on the productId.
    public ProductInfo getProductDetails(String productId){
       return dbMapper.load(ProductInfo.class, productId);
    }

    // Creates product, assigns unique productId.
    public ProductInfo createProduct(ProductInfo product){
        dbMapper.save(product);
        return product;
    }

    // Only allowed to update the status or Url of the product. ACTIVE/INACTIVE
    public ProductInfo updateProductDetails(ProductInfo product){
        ProductInfo productInfo = dbMapper.load(ProductInfo.class, product.getProductId());
        if(Objects.nonNull(productInfo)){
            if(!StringUtils.isEmpty(product.getProductStatus())) productInfo.setProductStatus(product.getProductStatus());
            if(!StringUtils.isEmpty(product.getProductUrl())) productInfo.setProductUrl(product.getProductUrl());
            dbMapper.save(productInfo);
            return productInfo;
        }else{
            throw new ProfileServiceException(ErrorCode.NO_PRODUCT_FOUND);
        }

    }

    public void deleteProduct(String productId){
        ProductInfo toDelete = dbMapper.load(ProductInfo.class, productId);
        // if item is present, then delete.
        if(Objects.nonNull(toDelete)){
            dbMapper.delete(toDelete);
        }else {
            throw new ProfileServiceException(ErrorCode.NO_PRODUCT_FOUND);
        }
    }

    public List<ProductInfo> getProductListWithStatus(String status) {
        Map<String, AttributeValue> eav = new HashMap<String,AttributeValue>();
        eav.put(":v1", new AttributeValue().withS(status));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("productStatus =:v1").withExpressionAttributeValues(eav);
        PaginatedScanList<ProductInfo> paginatedScanList = dbMapper.scan(ProductInfo.class, scanExpression);

        List<ProductInfo> productList = new ArrayList<>();
        paginatedScanList.stream().forEach(obj -> {
            productList.add(obj);
        });
        return productList;
    }
}
