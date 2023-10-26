package com.business.profiler.service.api.impl;

import com.business.profiler.contract.business.*;
import com.business.profiler.contract.common.ApprovalStatus;
import com.business.profiler.contract.common.ServiceRequest;
import com.business.profiler.contract.common.ServiceResponse;
import com.business.profiler.contract.common.Status;
import com.business.profiler.exceptions.ErrorCode;
import com.business.profiler.exceptions.ProfileServiceException;
import com.business.profiler.external.InvokeExtAPI;
import com.business.profiler.helper.ProductHelper;
import com.business.profiler.helper.SubscriptionHelper;
import com.business.profiler.helper.UserHelper;
import com.business.profiler.service.api.UserResources;
import com.business.profiler.util.Constants;
import com.business.profiler.util.ObjectMapperFactory;
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
public class UserProfileController implements UserResources {
    public static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserHelper userHelper;

    @Autowired
    private ProductHelper productHelper;

    @Autowired
    private InvokeExtAPI extAPI;

    @Autowired
    private SubscriptionHelper subscriptionHelper;

    //TODO Get the user Details with the user Id.
    @Override
    public ServiceResponse<User> getUserProfileDetailsApi(String userId) {
        logger.info("UserProfileController: getUserprofileDetails: Start:");
        try{
            User user = new User();
            user.setUserId(userId);
            User fetchedUser = userHelper.getUserDetails(user);

            ServiceResponse<User> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(fetchedUser);

            return response;
        }catch (ProfileServiceException pe){
            throw pe;
        }catch (Exception e){
            throw new ProfileServiceException(e);
        }

    }


    @Override
    public ServiceResponse<changeRequest> submitNewUserProfile(ServiceRequest<changeRequest> input){
        logger.info("Inside Submit new user request");
        try{
            if(Objects.isNull(input) || Objects.isNull(input.getPayload())){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }

            User user = input.getPayload().getUserDetails();
            List<Subscription> subs = user.getSubscriptionList();
            if(null==subs || subs.isEmpty()){
                throw new ProfileServiceException(ErrorCode.ATLEAST_ONE_SUBSCRIPTION_IS_MANDATORY);
            }
            changeRequest inReq = input.getPayload();
            if(StringUtils.isEmpty(inReq.getRequestStatus())){
                inReq.setRequestStatus(ApprovalStatus.PENDING_APPROVAL.name());
            }
            if(StringUtils.isEmpty(inReq.getRequestType())){
                inReq.setRequestType(Constants.CREATE);
            }
            // Save the request
            subs.forEach(sub -> sub.setStatus(ApprovalStatus.PENDING_APPROVAL.name()));
            changeRequest savedReq = userHelper.saveRequest(inReq);
            User newUserObj = new User();
            newUserObj = user;
            newUserObj.setSubscriptionList(null);

            // Now send request to other services for their approval.
            for(Subscription s : subs){
                changeRequest newChangeRequest = new changeRequest();
                newChangeRequest.setUserDetails(newUserObj);
                newChangeRequest.setRequestType(savedReq.getRequestType());
                newChangeRequest.setRequestId(savedReq.getRequestId());
                newChangeRequest.setRequestStatus(savedReq.getRequestStatus());

                    // convert the req obj to string
                String appReq = ObjectMapperFactory.getMapper().writeValueAsString(newChangeRequest);
                    // fetch product details.
                Product productDetails = productHelper.getProductDetails(s.getProductId());
                String url = productDetails.getProductUrl();
                    // call the api. before that fetch the product details and fetch the URL to be hit.
                String apiResponse = extAPI.invokeExternalApi(url, appReq);

                }
            ServiceResponse<changeRequest> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(savedReq);

            // Create payload to send request to other services.
            // store the request in request table.
            return response;

        } catch (ProfileServiceException pex){
            throw pex;
        }catch (Exception e){
            throw new ProfileServiceException(e);
        }
    }

    @Override
    public ServiceResponse<changeRequest> submitUpdateForUserProfile(ServiceRequest<changeRequest> input) {
        logger.info("Inside submit update user request API");
        try{
            if(Objects.isNull(input) || Objects.isNull(input.getPayload())){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            changeRequest inReq = input.getPayload();
            User user = input.getPayload().getUserDetails();
            if(StringUtils.isEmpty(inReq.getRequestStatus())){
                inReq.setRequestStatus(ApprovalStatus.PENDING_APPROVAL.name());
            }
            if(null == inReq.getRequestType()|| inReq.getRequestType().isEmpty()){
                inReq.setRequestType(Constants.UPDATE);
            }
            List<Subscription> subs = subscriptionHelper.getSubscriptionDetailsList(user.getUserId());
                if(Objects.isNull(subs)){
                    throw new ProfileServiceException(ErrorCode.NO_SUBSCRIPTIONS_FOUND_FOR_USER);
                }else{
                    subs.removeIf(s -> Constants.INACTIVE.equals(s.getStatus()));
                    subs.forEach(sub -> sub.setStatus(ApprovalStatus.PENDING_APPROVAL.name()));
                    user.setSubscriptionList(subs);
                    for(Subscription s: subs){
                        String appReq = ObjectMapperFactory.getMapper().writeValueAsString(inReq);
                        // fetch product details.
                        Product productDetails = productHelper.getProductDetails(s.getProductId());
                        String url = productDetails.getProductUrl();
                        // call the api. before that fetch the product details and fetch the URL to be hit.
                        String apiResponse = extAPI.invokeExternalApi(url, appReq);
                    }
                    inReq.setUserDetails(user);
                    User userUpdateVerificationStatus = new User();
                    userUpdateVerificationStatus.setUserId(user.getUserId());
                    userHelper.updateUserDetails(userUpdateVerificationStatus);
                    changeRequest req = userHelper.saveRequest(inReq);
                    inReq.setRequestType(req.getRequestType());
                    inReq.setRequestId(req.getRequestId());
                    inReq.setRequestStatus(req.getRequestStatus());
                }
            ServiceResponse<changeRequest> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(inReq);
            return response;
        } catch (ProfileServiceException pe){
            throw pe;
        } catch(Exception ex){
            throw new ProfileServiceException(ex);
        }
    }

    /*
    A
     */
    @Override
    public ServiceResponse<changeRequest> approveRequestForCreateAndUpdate(ServiceRequest<RequestApproval> input) {
        logger.info(("Inside approve request API "));
        try{
            RequestApproval apprRequest = input.getPayload();
            if(Objects.isNull(apprRequest)){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            String reqId = apprRequest.getRequestId();
            if(StringUtils.isEmpty(reqId)){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            changeRequest request = userHelper.getRequestDetails(reqId);
            if(ApprovalStatus.PENDING_APPROVAL.name().equals(request.getRequestStatus())){
                if(null!=apprRequest.getApprovalStatus() && !apprRequest.getApprovalStatus().isEmpty()){
                    if(ApprovalStatus.DENIED.name().equals(apprRequest.getApprovalStatus())){
                        request.setRequestStatus(ApprovalStatus.DENIED.name());
                        request.getUserDetails().getSubscriptionList().stream()
                                .filter(subscription -> apprRequest.getProductId().equals(subscription.getProductId()))
                                .forEach(subscription -> subscription.setStatus(ApprovalStatus.DENIED.name()));

                    } else if (ApprovalStatus.APPROVED.name().equals(apprRequest.getApprovalStatus())) {
                        request.getUserDetails().getSubscriptionList().stream()
                                .filter(subscription -> apprRequest.getProductId().equals(subscription.getProductId()))
                                .forEach(subscription -> subscription.setStatus(ApprovalStatus.APPROVED.name()));
                    }

                    List<Subscription> subscriptionList = request.getUserDetails().getSubscriptionList();
                    int size = subscriptionList.size();
                    int count = 0;
                    for(Subscription s : subscriptionList){
                        if(ApprovalStatus.APPROVED.name().equals(s.getStatus())) count++;
                    }
                    if(count==size){
                        request.setRequestStatus(ApprovalStatus.APPROVED.name());
                        if(Constants.CREATE.equals(request.getRequestType())){
                            // save user in user table.
                            request.getUserDetails().setSubscriptionStatus(Constants.ACTIVE);
                            User createdUser = userHelper.createUser(request.getUserDetails());
                            // save subscriptions in subscription table.
                            String userId = createdUser.getUserId();
                            request.getUserDetails().setUserId(userId);
                            for (Subscription s : subscriptionList){
                                s.setUserId(userId);
                                s.setStatus(Constants.ACTIVE);
                                Subscription sub = subscriptionHelper.createSubscription(s); // saving in the dao.
                                s.setStatus(ApprovalStatus.APPROVED.name());
                            }
                        }else if(Constants.UPDATE.equals(request.getRequestType())){
                            // update the user details after receiving update approval
                            User updatedUser = userHelper.updateUserDetails(request.getUserDetails());
                            updatedUser.setSubscriptionList(subscriptionList);
                            request.setUserDetails(updatedUser);
                        }
                    }
                    request = userHelper.saveRequest(request);
                }
            }else {
                throw new ProfileServiceException(ErrorCode.REQUEST_IN_STATUS, request.getRequestStatus());
            }

            ServiceResponse<changeRequest> response = new ServiceResponse<>();
            response.setStatus(Status.SUCCESS);
            response.setResult(request);

            return response;
        }catch (ProfileServiceException ex){
            throw ex;
        }catch (Exception ex){
            throw new ProfileServiceException(ex);
        }

    }

    @Override
    public ServiceResponse<List<changeRequest>> getChangeRequestList(String status) {
        logger.info("Inside get change request list API");
        try{
            if(status.isEmpty()){
                // set to default value
                status = Constants.ACTIVE;
            }
            List<changeRequest> requestList = userHelper.getChangeRequestList(status);
            ServiceResponse<List<changeRequest>> response = new ServiceResponse<>();
            response.setResult(requestList);
            response.setStatus(Status.SUCCESS);
            return response;
        }catch (ProfileServiceException e){
            throw e;
        }
        catch (Exception e){
            throw new ProfileServiceException(e);
        }

    }

    @Override
    public ServiceResponse<String> retryUserRequestMessage(String requestId) {
        logger.info("Inside retry UserRequest");
        // requests will be resent to all product subscriptions.
        try{
            if(StringUtils.isEmpty(requestId)){
                throw new ProfileServiceException(ErrorCode.INPUT_VALIDATION_ERROR);
            }
            changeRequest request = userHelper.getRequestDetails(requestId);
            if(Objects.isNull(request)){
                throw new ProfileServiceException(ErrorCode.REQUEST_DOES_NOT_EXIST, requestId);
            }
            if(ApprovalStatus.PENDING_APPROVAL.name().equals(request.getRequestStatus())){
                List<Subscription> subs = request.getUserDetails().getSubscriptionList();
                String strResponse = "";
                for(Subscription s: subs){
                    changeRequest newChangeRequest = new changeRequest();
                    User newUserObj = new User();
                    newUserObj = request.getUserDetails();
                    newUserObj.setSubscriptionList(null);
                    newChangeRequest.setUserDetails(newUserObj);
                    newChangeRequest.setRequestType(request.getRequestType());
                    newChangeRequest.setRequestId(request.getRequestId());
                    newChangeRequest.setRequestStatus(request.getRequestStatus());

                    String appReq = ObjectMapperFactory.getMapper().writeValueAsString(newChangeRequest);
                    // fetch product details.
                    Product productDetails = productHelper.getProductDetails(s.getProductId());
                    String url = productDetails.getProductUrl();
                    // call the api. before that fetch the product details and fetch the URL to be hit.
                    String apiResponse = extAPI.invokeExternalApi(url, appReq);
                    strResponse = strResponse.concat(apiResponse);
                    strResponse = strResponse.concat("  ");
                }
                ServiceResponse<String> response = new ServiceResponse<>();
                response.setStatus(Status.SUCCESS);
                response.setResult(strResponse);
                return response;
            }
        }catch (ProfileServiceException pex){
            throw pex;
        }catch (Exception e){
            throw new ProfileServiceException(e);
        }
        return null;
    }
}
