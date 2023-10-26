package com.business.profiler.service.api;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import com.business.profiler.contract.business.Subscription;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.contract.common.ServiceResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/subscription")
//@Api(value = "/v1/subscription", tags = {"subscription"})
public interface SubscriptionResources {

//    @ApiOperation(value = "getSubscriptionDetails")
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<List<Subscription>> getSubscriptionDetails(@RequestParam(name = "userId") String userId);

    @ResponseBody
//    @ApiOperation(value = "createSubscription")
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<Subscription> addSubscription(@RequestBody ServiceRequest<Subscription> input);

    @ResponseBody
//    @ApiOperation(value = "updateSubscriptionStatus")
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<Subscription> updateSubscriptionStatus(@RequestBody ServiceRequest<Subscription> input);




}
