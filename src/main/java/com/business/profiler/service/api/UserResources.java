package com.business.profiler.service.api;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import com.business.profiler.contract.business.RequestApproval;
import com.business.profiler.contract.business.changeRequest;
import com.business.profiler.contract.business.User;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.contract.common.ServiceResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/userProfile")
//@Api(value = "/v1/userProfile", tags = {"userProfile"})
public interface UserResources {

//    @ApiOperation(value = "getUserProfile")
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<User> getUserProfileDetailsApi(@RequestParam(name = "userId") String userId);

    @ResponseBody
//    @ApiOperation("createUserProfile")
    @PostMapping(path = "/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<changeRequest> submitNewUserProfile(@RequestBody ServiceRequest<changeRequest> input);

    @ResponseBody
//    @ApiOperation(value = "updateUserProfileDetails")
    @PutMapping(path = "/request", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<changeRequest> submitUpdateForUserProfile(@RequestBody ServiceRequest<changeRequest> input);

    @ResponseBody
    @PostMapping(path = "/request/approve", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<changeRequest> approveRequestForCreateAndUpdate(@RequestBody ServiceRequest<RequestApproval> input);

    @ResponseBody
    @GetMapping(path = "/request/list", produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<List<changeRequest>> getChangeRequestList(@RequestParam(name="status") String status);

    @ResponseBody
    @PostMapping(path= "/request/retry", produces = MediaType.APPLICATION_JSON_VALUE)
    ServiceResponse<String> retryUserRequestMessage(@RequestParam(name="requestId") String requestId);



}
