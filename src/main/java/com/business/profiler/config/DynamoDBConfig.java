package com.business.profiler.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.business.profiler.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;

@Configuration
public class DynamoDBConfig {

    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;
    @Value("${aws.accessKey}")
    private String awsAccessKey;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Bean
    public DynamoDBMapper dynamoDBMapper() {
        AmazonDynamoDB amazonDynamoDB
                = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, "us-west-2"))
                .withCredentials(new AWSStaticCredentialsProvider(amazonAWSCredentials())).build();

        DynamoDBMapper dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB, DynamoDBMapperConfig.DEFAULT);
        init(dynamoDBMapper, amazonDynamoDB);
        return dynamoDBMapper;
    }

    @Bean
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

    public void init(DynamoDBMapper dynamoDBMapper, AmazonDynamoDB client) {

        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(ProductInfo.class);
        CreateTableRequest tableRequest2 = dynamoDBMapper.generateCreateTableRequest(UserInfo.class);
        CreateTableRequest tableRequest3 = dynamoDBMapper.generateCreateTableRequest(SubscriptionInfo.class);
        CreateTableRequest tableRequest4 = dynamoDBMapper.generateCreateTableRequest(Request.class);
        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        tableRequest2.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        tableRequest3.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        tableRequest4.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
        if (TableUtils.createTableIfNotExists(client, tableRequest)) {
            System.out.println("Table created");
        }
        if (TableUtils.createTableIfNotExists(client, tableRequest2)) {
            System.out.println("Table created");
        }
        if (TableUtils.createTableIfNotExists(client, tableRequest3)) {
            System.out.println("Table created");
        }
        if (TableUtils.createTableIfNotExists(client, tableRequest4)) {
            System.out.println("Table created");
        }

    }

}
