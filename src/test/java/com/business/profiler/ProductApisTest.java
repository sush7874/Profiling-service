package com.business.profiler;

import com.business.profiler.contract.business.Product;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.contract.common.ServiceResponse;
import com.business.profiler.contract.common.Status;
import com.business.profiler.util.ObjectMapperFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProductApisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestHelper helper;


    @Test
    public void testCreateProductApi() throws Exception{
        ServiceRequest<Product> requestProd = helper.createProductRequest("createProductInput.json");
        String ProdStr = requestProd.toString();

        MvcResult result = mvc.perform(post("/v1/products").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(ProdStr)).andReturn();
        String resultString = result.getResponse().getContentAsString();
        TypeReference<ServiceResponse<Product>> ref = new TypeReference<ServiceResponse<Product>>() {};
        ServiceResponse<Product> response = ObjectMapperFactory.getMapper().readValue(resultString, ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());

    }

    @Test
    public void testGetProductApi() throws Exception{
        // create product
        ServiceRequest<Product> requestProd = helper.createProductRequest("createProductInput.json");
        String ProdStr = requestProd.toString();

        MvcResult result = mvc.perform(post("/v1/products").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(ProdStr)).andReturn();
        String resultString = result.getResponse().getContentAsString();
        TypeReference<ServiceResponse<Product>> ref = new TypeReference<ServiceResponse<Product>>() {};
        ServiceResponse<Product> response = ObjectMapperFactory.getMapper().readValue(resultString, ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());

        // get product details
        String productId = response.getResult().getProductId();
        MvcResult resultGetApi = mvc.perform(get("/v1/products" + "?" + "productId=" + productId).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content("")).andReturn();

        ServiceResponse<Product> fetchedProduct = ObjectMapperFactory.getMapper().readValue(resultGetApi.getResponse().getContentAsString(), ref);

        assertEquals(Status.SUCCESS.name(), fetchedProduct.getStatus().name());

    }

    @Test
    public void testExceptionForGetProductApi() throws Exception{

        MvcResult resultGetApi = mvc.perform(get("/v1/products" + "?" + "productId=" + "RandomId").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content("")).andReturn();
        TypeReference<ServiceResponse<Product>> ref = new TypeReference<ServiceResponse<Product>>() {};
        ServiceResponse<Product> fetchedProduct = ObjectMapperFactory.getMapper().readValue(resultGetApi.getResponse().getContentAsString(), ref);


        assertEquals(Status.FAIL.name(), fetchedProduct.getStatus().name());
        assertEquals("400.1", fetchedProduct.getErrors().get(0).getErrorCode());
        assertEquals("No product found with ProductId RandomId", fetchedProduct.getErrors().get(0).getErrorDescription());
    }

    @Test
    public void testUpdateProductStatusApi() throws Exception{
        //create product
        ServiceRequest<Product> requestProd = helper.createProductRequest("createProductInput.json");
        String ProdStr = requestProd.toString();

        MvcResult result = mvc.perform(post("/v1/products").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(ProdStr)).andReturn();
        String resultString = result.getResponse().getContentAsString();
        TypeReference<ServiceResponse<Product>> ref = new TypeReference<ServiceResponse<Product>>() {};
        ServiceResponse<Product> response = ObjectMapperFactory.getMapper().readValue(resultString, ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());
        Product savedProd = response.getResult();
        // update product details
        Product toUpdate = new Product();
        toUpdate.setProductId(savedProd.getProductId());
        toUpdate.setProductUrl("http:/11213.com");
        toUpdate.setStatus("INACTIVE");
        toUpdate.setName("YSheets");
        ServiceRequest<Product> updateProdReq = new ServiceRequest<>();
        updateProdReq.setPayload(toUpdate);

        MvcResult updateResult = mvc.perform(put("/v1/products").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(updateProdReq.toString())).andReturn();

        ServiceResponse<Product> updatedProduct = ObjectMapperFactory.getMapper().readValue(updateResult.getResponse().getContentAsString(), ref);

        assertEquals("TSheets", updatedProduct.getResult().getName());
        assertEquals("http:/11213.com", updatedProduct.getResult().getProductUrl());
        assertEquals("INACTIVE", updatedProduct.getResult().getStatus());

    }

    @Test
    public void deleteProductTestApi() throws Exception{

        //create product
        ServiceRequest<Product> requestProd = helper.createProductRequest("createProductInput.json");
        String ProdStr = requestProd.toString();

        MvcResult result = mvc.perform(post("/v1/products").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(ProdStr)).andReturn();
        String resultString = result.getResponse().getContentAsString();
        TypeReference<ServiceResponse<Product>> ref = new TypeReference<ServiceResponse<Product>>() {};
        ServiceResponse<Product> response = ObjectMapperFactory.getMapper().readValue(resultString, ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());
        String savedProdId = response.getResult().getProductId();

        MvcResult deleteResponse = mvc.perform(delete("/v1/products" + "?productId=" + savedProdId)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("")).andReturn();

        TypeReference<ServiceResponse<String>> deleteRef = new TypeReference<ServiceResponse<String>>() {};
        ServiceResponse<String> deletedRespObj = ObjectMapperFactory.getMapper().readValue(deleteResponse.getResponse().getContentAsString(), deleteRef);

        assertEquals("Product Details deleted successfully for productId: "+ savedProdId, deletedRespObj.getResult());



    }
}
