package com.business.profiler.helper;

import com.business.profiler.contract.business.Product;
import com.business.profiler.contract.business.Subscription;
import com.business.profiler.dao.ProductDao;
import com.business.profiler.dao.SubscriptionDao;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.mappers.ProductEntityMapper;
import com.business.profiler.model.ProductInfo;
import com.business.profiler.model.SubscriptionInfo;
import com.business.profiler.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ProductHelper {
    public static final Logger logger = LoggerFactory.getLogger(ProductHelper.class);

    @Autowired
    private ProductEntityMapper mapper;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private SubscriptionDao subscriptionDao;

    @Autowired
    private SubscriptionHelper sHelper;

    public Product getProductDetails(String productId){
        logger.info("GetProductDetailsApi: Starts");
        ProductInfo productInfo = productDao.getProductDetails(productId);
        if(Objects.nonNull(productInfo)){
            return mapper.reverseMap(productInfo);
        }else {
            throw new ProfileServiceException(ErrorCode.NO_PRODUCT_FOUND, productId);
        }
    }

    public Product createProduct(Product product){
        logger.info("CreateProductApi: Starts");
        ProductInfo prodinfo = mapper.map(product);

        ProductInfo productInfo = productDao.createProduct(prodinfo);
        if(Objects.nonNull(productInfo)){
            return mapper.reverseMap(productInfo);
        }else {
            throw new ProfileServiceException(ErrorCode.UNABLE_TO_CREATE_PRODUCT);
        }

    }

    public Product updateProductDetails(Product product){
        logger.info("Product Update details API");
        try{
            ProductInfo updatedProd = mapper.map(product);
            updatedProd = productDao.updateProductDetails(updatedProd);
            // If product status is updated to inactive, all the subscriptions which are related to the product have to be moved to inactive status
            if(Constants.INACTIVE.equals(updatedProd.getProductStatus())){
                // set all subscriptions with the productId as inactive
                List<Subscription> subList =  sHelper.getSubscriptionDetailsListWithProductId(updatedProd.getProductId());
                if(null!=subList && !subList.isEmpty()){
                    for(Subscription s : subList){
                        s.setStatus(Constants.INACTIVE);
                        sHelper.updateSubscriptionStatus(s);
                    }
                }
            }
            return mapper.reverseMap(updatedProd);
        }catch (ProfileServiceException pex){
            throw pex;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }

    public void deleteProduct(String productId){
        logger.info("Delete product APi");
        try{
            productDao.deleteProduct(productId);
            // when product is deleted, move the status of all subscriptions to that product to inactive/discontinued.
            List<Subscription> subList =  sHelper.getSubscriptionDetailsListWithProductId(productId);
            if(null!=subList && !subList.isEmpty()) {
                for (Subscription s : subList) {
                    s.setStatus(Constants.INACTIVE);
                    sHelper.updateSubscriptionStatus(s);
                }
            }
        }catch (ProfileServiceException pex){
            throw pex;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }

    public List<Product> getProductsList(String status){
        // Fetch only active status products.
        List<ProductInfo> resultDao = productDao.getProductListWithStatus(status);
        if(!resultDao.isEmpty()){
            return mapper.eGetProductList(resultDao);
        }else {
            throw new ProfileServiceException(ErrorCode.NO_PRODUCTS_FOUND_WITH_STATUS, status);
        }



    }
}
