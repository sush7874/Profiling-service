package com.business.profiler.mappers;

import com.business.profiler.contract.business.Product;
import com.business.profiler.model.ProductInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductEntityMapper implements Mapper<Product, ProductInfo> {

    @Override
    public Product reverseMap(ProductInfo productInfo){
        Product product = new Product();
        product.setProductId(productInfo.getProductId());
        product.setDescription(productInfo.getProductDescription());
        product.setName(productInfo.getProductName());
        product.setCreatedDate(productInfo.getProductCreatedDate());
        product.setStatus(productInfo.getProductStatus());
        product.setProductUrl(productInfo.getProductUrl());

        return product;
    }

    @Override
    public ProductInfo map(Product product){
        ProductInfo productInfo = new ProductInfo();
        productInfo.setProductCreatedDate(product.getCreatedDate());
        productInfo.setProductDescription(product.getDescription());
        productInfo.setProductName(product.getName());
        productInfo.setProductId(product.getProductId());
        productInfo.setProductStatus(product.getStatus());
        productInfo.setProductUrl(product.getProductUrl());

        return productInfo;
    }

    public List<ProductInfo> mGetProductList(List<Product> prodList){
        List<ProductInfo> mProdList = new ArrayList<>();
        for(Product p: prodList){
            ProductInfo prodInfo = map(p);
            mProdList.add(prodInfo);
        }
        return mProdList;
    }

    public List<Product> eGetProductList(List<ProductInfo> prodList){
        List<Product> eProdList = new ArrayList<>();
        for(ProductInfo p: prodList){
            Product prod = reverseMap(p);
            eProdList.add(prod);
        }
        return eProdList;
    }


}
