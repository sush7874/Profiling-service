package com.business.profiler;

import com.business.profiler.contract.business.Product;
import com.business.profiler.contract.business.RequestApproval;
import com.business.profiler.contract.business.User;
import com.business.profiler.contract.business.changeRequest;
import com.business.profiler.contract.common.ApprovalStatus;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserAndRequestApisTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TestHelper helper;


    @Test
    public void testForCreateUserRequestApi() throws Exception{

        // change request is created.
        ServiceRequest<changeRequest> requestProd = helper.createChangeRequest("createUserRequest.json");
        String ProdStr = requestProd.toString();

        MvcResult result = mvc.perform(post("/v1/userProfile/request").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(ProdStr)).andReturn();
        String resultString = result.getResponse().getContentAsString();
        TypeReference<ServiceResponse<changeRequest>> ref = new TypeReference<ServiceResponse<changeRequest>>() {};
        ServiceResponse<changeRequest> response = ObjectMapperFactory.getMapper().readValue(resultString, ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());

        String requestId = response.getResult().getRequestId();
        String[] prodIds = {response.getResult().getUserDetails().getSubscriptionList().get(0).getProductId(), response.getResult().getUserDetails().getSubscriptionList().get(1).getProductId()};

        changeRequest savedCR = new changeRequest();

        // now send approvals for the products
        for(String prodId : prodIds){
            RequestApproval appForProd1 = new RequestApproval();
            appForProd1.setRequestId(requestId);
            appForProd1.setApprovalStatus(ApprovalStatus.APPROVED.name());
            appForProd1.setProductId(prodId);

            ServiceRequest<RequestApproval> reqApproval = new ServiceRequest<>();
            reqApproval.setPayload(appForProd1);
            String reqString = reqApproval.toString();

            MvcResult approvedResponse = mvc.perform(post("/v1/userProfile/request/approve").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON).content(reqString)).andReturn();

            TypeReference<ServiceResponse<changeRequest>> crRef = new TypeReference<ServiceResponse<changeRequest>>() {};
            ServiceResponse<changeRequest> approvedRespObj = ObjectMapperFactory.getMapper()
                    .readValue(approvedResponse.getResponse().getContentAsString(), crRef);

            assertEquals(Status.SUCCESS.name(),approvedRespObj.getStatus().name());
            savedCR = approvedRespObj.getResult();

        }

        assertNotEquals(null, savedCR.getUserDetails().getUserId());

    }

    @Test
    public void testForCreateUserRequestDenialApi() throws Exception{

        // change request is created.
        ServiceRequest<changeRequest> requestProd = helper.createChangeRequest("createUserRequest.json");
        String ProdStr = requestProd.toString();

        MvcResult result = mvc.perform(post("/v1/userProfile/request").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(ProdStr)).andReturn();
        String resultString = result.getResponse().getContentAsString();
        TypeReference<ServiceResponse<changeRequest>> ref = new TypeReference<ServiceResponse<changeRequest>>() {};
        ServiceResponse<changeRequest> response = ObjectMapperFactory.getMapper().readValue(resultString, ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());

        String requestId = response.getResult().getRequestId();
        String[] prodIds = {response.getResult().getUserDetails().getSubscriptionList().get(0).getProductId(), response.getResult().getUserDetails().getSubscriptionList().get(1).getProductId()};

        changeRequest savedCR = new changeRequest();

        // now send approvals for the products
        RequestApproval appForProd1 = new RequestApproval();
        appForProd1.setRequestId(requestId);
        appForProd1.setApprovalStatus(ApprovalStatus.DENIED.name());
        appForProd1.setProductId(prodIds[0]);

        ServiceRequest<RequestApproval> reqApproval = new ServiceRequest<>();
        reqApproval.setPayload(appForProd1);
        String reqString = reqApproval.toString();

        MvcResult approvedResponse = mvc.perform(post("/v1/userProfile/request/approve").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON).content(reqString)).andReturn();

        TypeReference<ServiceResponse<changeRequest>> crRef = new TypeReference<ServiceResponse<changeRequest>>() {};
        ServiceResponse<changeRequest> approvedRespObj = ObjectMapperFactory.getMapper()
                    .readValue(approvedResponse.getResponse().getContentAsString(), crRef);

        assertEquals(Status.SUCCESS.name(),approvedRespObj.getStatus().name());
        savedCR = approvedRespObj.getResult();

        assertEquals(null, savedCR.getUserDetails().getUserId());

        // send approval from different product
        // it will throw error as the request is already moved to a denied status

        appForProd1.setProductId(prodIds[0]);
        appForProd1.setApprovalStatus(ApprovalStatus.APPROVED.name());

        reqApproval.setPayload(appForProd1);
        reqString = reqApproval.toString();

        approvedResponse = mvc.perform(post("/v1/userProfile/request/approve").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(reqString)).andReturn();

        approvedRespObj = ObjectMapperFactory.getMapper()
                .readValue(approvedResponse.getResponse().getContentAsString(), crRef);

        assertEquals(Status.FAIL.name(), approvedRespObj.getStatus().name());
        assertEquals("400.15", approvedRespObj.getErrors().get(0).getErrorCode());
        assertEquals("Request is in status DENIED", approvedRespObj.getErrors().get(0).getErrorDescription());


    }

    @Test
    public void testForUpdateUserRequestApi() throws Exception{

        // change request is created.
        ServiceRequest<changeRequest> requestProd = helper.createChangeRequest("createUserRequest.json");
        String ProdStr = requestProd.toString();

        MvcResult result = mvc.perform(post("/v1/userProfile/request").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(ProdStr)).andReturn();
        String resultString = result.getResponse().getContentAsString();
        TypeReference<ServiceResponse<changeRequest>> ref = new TypeReference<ServiceResponse<changeRequest>>() {};
        ServiceResponse<changeRequest> response = ObjectMapperFactory.getMapper().readValue(resultString, ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());

        String requestId = response.getResult().getRequestId();
        String[] prodIds = {response.getResult().getUserDetails().getSubscriptionList().get(0).getProductId(), response.getResult().getUserDetails().getSubscriptionList().get(1).getProductId()};

        changeRequest savedCR = new changeRequest();

        // now send approvals for the products
        for(String prodId : prodIds){
            RequestApproval appForProd1 = new RequestApproval();
            appForProd1.setRequestId(requestId);
            appForProd1.setApprovalStatus(ApprovalStatus.APPROVED.name());
            appForProd1.setProductId(prodId);

            ServiceRequest<RequestApproval> reqApproval = new ServiceRequest<>();
            reqApproval.setPayload(appForProd1);
            String reqString = reqApproval.toString();

            MvcResult approvedResponse = mvc.perform(post("/v1/userProfile/request/approve").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON).content(reqString)).andReturn();

            TypeReference<ServiceResponse<changeRequest>> crRef = new TypeReference<ServiceResponse<changeRequest>>() {};
            ServiceResponse<changeRequest> approvedRespObj = ObjectMapperFactory.getMapper()
                    .readValue(approvedResponse.getResponse().getContentAsString(), crRef);

            assertEquals(Status.SUCCESS.name(),approvedRespObj.getStatus().name());
            savedCR = approvedRespObj.getResult();

        }

        assertNotEquals(null, savedCR.getUserDetails().getUserId());

        // now update some field in the created user.

        changeRequest updateRequest = new changeRequest();
        User toUpdate = new User();
        toUpdate.setUserId(savedCR.getUserDetails().getUserId());
        toUpdate.setAddress("5th floor shantiniketan mall");
        toUpdate.setName("McDonalds Shantiniketan");
        toUpdate.setLegalName("MyFoodsFranchiser");

        updateRequest.setUserDetails(toUpdate);
        ServiceRequest<changeRequest> updateUserRequest = new ServiceRequest<>();
        updateUserRequest.setPayload(updateRequest);
        result = mvc.perform(put("/v1/userProfile/request").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content(updateUserRequest.toString())).andReturn();

        response = ObjectMapperFactory.getMapper().readValue(result.getResponse().getContentAsString(), ref);

        assertEquals(Status.SUCCESS.name(), response.getStatus().name());

        // now approve the change.
        // now send approvals for the products
        for(String prodId : prodIds){
            RequestApproval appForProd1 = new RequestApproval();
            appForProd1.setRequestId(response.getResult().getRequestId());
            appForProd1.setApprovalStatus(ApprovalStatus.APPROVED.name());
            appForProd1.setProductId(prodId);

            ServiceRequest<RequestApproval> reqApproval = new ServiceRequest<>();
            reqApproval.setPayload(appForProd1);
            String reqString = reqApproval.toString();

            MvcResult approvedResponse = mvc.perform(post("/v1/userProfile/request/approve").accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON).content(reqString)).andReturn();

            TypeReference<ServiceResponse<changeRequest>> crRef = new TypeReference<ServiceResponse<changeRequest>>() {};
            ServiceResponse<changeRequest> approvedRespObj = ObjectMapperFactory.getMapper()
                    .readValue(approvedResponse.getResponse().getContentAsString(), crRef);

            assertEquals(Status.SUCCESS.name(),approvedRespObj.getStatus().name());
            savedCR = approvedRespObj.getResult();

        }

        User updatedUser = savedCR.getUserDetails();
        assertEquals("5th floor shantiniketan mall", updatedUser.getAddress());
        assertEquals("McDonalds Shantiniketan", updatedUser.getName());
        assertNotEquals("MyFoodsFranchiser", updatedUser.getLegalName());
    }


}
