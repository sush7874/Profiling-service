package com.business.profiler.service.api;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import com.business.profiler.contract.business.Product;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.contract.common.ServiceResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/products")
//@Api(value = "/v1/products", tags = {"products"})
public interface ProductResources {

//    @ApiOperation(value = "getProductDetails")
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody ServiceResponse<Product> getProductDetailsApi(@RequestParam(name = "productId") String productId);

    @ResponseBody
//    @ApiOperation(value = "createProduct")
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<Product> createProductApi(@RequestBody ServiceRequest<Product> input);

    @ResponseBody
//    @ApiOperation(value = "/updateProduct")
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<Product> updateProductInfoApi(@RequestBody ServiceRequest<Product> input);

//    @ApiOperation(value = "deleteProduct")
    @DeleteMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<String> deleteProductApi(@RequestParam(name = "productId") String productId);

    @ResponseBody
    @GetMapping(path="/list", produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<List<Product>> getProductsListApi(@RequestParam(name="status") String status);





}
