package com.musicapp.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import javax.annotation.PostConstruct;

@Configuration
public class DynamoDBInitializer {

    @Autowired
    private AmazonDynamoDB dynamoClient;

    @PostConstruct
    public void init() {
        createSubscriptionsTable();
        insertTestData();
    }

    private void createSubscriptionsTable() {
        String tableName = "subscriptions";
        try {
            dynamoClient.describeTable(tableName);
            System.out.println("Table " + tableName + " already exists.");
        } catch (ResourceNotFoundException e) {
            CreateTableRequest request = new CreateTableRequest()
                    .withTableName(tableName)
                    .withKeySchema(
                            new KeySchemaElement("user_id", KeyType.HASH),
                            new KeySchemaElement("title", KeyType.RANGE)
                    )
                    .withAttributeDefinitions(
                            new AttributeDefinition("user_id", ScalarAttributeType.S),
                            new AttributeDefinition("title", ScalarAttributeType.S)
                    )
                    .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L));

            dynamoClient.createTable(request);
            System.out.println("Created table: " + tableName);
        }
    }

    private void insertTestData() {
        DynamoDB dynamoDB = new DynamoDB(dynamoClient);


        Table subscriptionsTable = dynamoDB.getTable("subscriptions");
        subscriptionsTable.putItem(new Item()
                .withPrimaryKey("user_id", "s39456436@student.rmit.edu.au", "title", "Sentimental Heart")
                .withString("artist", "She & Him"));

        subscriptionsTable.putItem(new Item()
                .withPrimaryKey("user_id", "s39456433@student.rmit.edu.au", "title", "Fall Line")
                .withString("artist", "Jack Johnson"));

        System.out.println("Inserted subscription test data");
    }
}