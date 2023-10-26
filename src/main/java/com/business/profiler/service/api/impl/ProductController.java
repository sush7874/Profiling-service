package com.business.profiler.service.api.impl;

import com.business.profiler.contract.business.Product;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.contract.common.ServiceResponse;
import com.business.profiler.contract.common.Status;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.helper.ProductHelper;
import com.business.profiler.service.api.ProductResources;
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
public class ProductController implements ProductResources {

    public static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductHelper productHelper;

    @Override
    public ServiceResponse<Product> getProductDetailsApi(String productId) {
        logger.info("ProductController : getProductDetails: Start");
        if(logger.isDebugEnabled()){
            logger.debug("productId is {}", productId);
        }
        if(StringUtils.isEmpty(productId)){
            throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
        }

        Product prodDetails = productHelper.getProductDetails(productId);
        ServiceResponse<Product> response = new ServiceResponse<>();
        response.setStatus(Status.SUCCESS);
        response.setResult(prodDetails);

        logger.info("ProductController: getProductDetails: End");
        return response;
    }

    @Override
    public ServiceResponse<Product> createProductApi(ServiceRequest<Product> input) {
        logger.info("ProductController: createProductApi: Start");
        try{
            if(Objects.isNull(input)){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            // get the input payload
            // pass to the helper class to create new obj
            // envelope the response in service response
            //
            Product inputPayload = input.getPayload();
            Product createdProduct = productHelper.createProduct(inputPayload);

            ServiceResponse<Product> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(createdProduct);
            return response;
        }catch (ProfileServiceException pe){
            throw pe;
        }catch (Exception e){
            throw new ProfileServiceException(e);
        }

    }

    @Override
    public ServiceResponse<Product> updateProductInfoApi(ServiceRequest<Product> input) {
        logger.info("Inside update Product Info API ");
        // Allowing update of Product Status and Product URL only

        try{
            if(Objects.isNull(input) || Objects.isNull(input.getPayload())){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            Product toUpdate = input.getPayload();
            Product updated = productHelper.updateProductDetails(toUpdate);

            ServiceResponse<Product> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(updated);
            return response;

        }catch (ProfileServiceException pe){
            throw pe;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }

    @Override
    public ServiceResponse<String> deleteProductApi(String productId) {
        logger.info("Inside delete product Api");

        try{
            if(StringUtils.isEmpty(productId)){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            productHelper.deleteProduct(productId);
            ServiceResponse<String> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult("Product Details deleted successfully for productId: " + productId);
            return response;
        }catch (ProfileServiceException pe){
            throw pe;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }

    @Override
    public ServiceResponse<List<Product>> getProductsListApi(String status) {
        logger.info("Inside Get Products list API");
        try{
            if(StringUtils.isEmpty(status)){
                // set default as ACTIVE
                status = Constants.ACTIVE;
            }
            List<Product> productList = productHelper.getProductsList(status);
            ServiceResponse<List<Product>> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(productList);
            return response;
        }catch (ProfileServiceException pe){
            throw pe;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }
}
