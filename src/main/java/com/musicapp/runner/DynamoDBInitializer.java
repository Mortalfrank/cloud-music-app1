package com.musicapp.runner;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapp.model.Music;
import com.musicapp.model.MusicWrapper;
import com.musicapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Profile("init-db") // This class only runs when the profile 'init-db' is enabled
@Component
public class DynamoDBInitializer implements CommandLineRunner {

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    @Autowired
    private DynamoDBMapper dynamoDBMapper;

    /**
     * Main runner method that executes on application startup (if profile matches).
     * Responsible for resetting tables, loading music data, adding users, and setting up test data.
     */
    @Override
    public void run(String... args) throws Exception {
        recreateTables();               // Recreate 'music' and 'login' tables
        importMusicData();             // Load music data from 2025a1.json
        addTestUsers();                // Populate login table with 10 test users
        createSubscriptionsTable();   // Create subscriptions table if it doesn't exist
        insertSubscriptionTestData(); // Insert test subscription data
    }

    /**
     * Deletes and recreates required tables: music and login.
     */
    private void recreateTables() {
        deleteTableIfExists("music");
        createMusicTable();
        waitForTableToBecomeActive("music");

        deleteTableIfExists("login");
        createLoginTable();
        waitForTableToBecomeActive("login");
    }

    /**
     * Deletes a table if it exists, and waits for full deletion before proceeding.
     */
    private void deleteTableIfExists(String tableName) {
        try {
            amazonDynamoDB.deleteTable(tableName);
            System.out.println("Deleting table: " + tableName);
            boolean tableExists = true;
            while (tableExists) {
                try {
                    Thread.sleep(1000);
                    amazonDynamoDB.describeTable(tableName);
                } catch (ResourceNotFoundException e) {
                    tableExists = false;
                    System.out.println("Table " + tableName + " deleted successfully");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted while waiting for table deletion");
                    break;
                }
            }
        } catch (ResourceNotFoundException e) {
            System.out.println("Table does not exist: " + tableName);
        }
    }

    /**
     * Creates the 'music' table with title and artist as keys,
     * and adds a global secondary index for year and album.
     */
    private void createMusicTable() {
        CreateTableRequest request = new CreateTableRequest()
                .withTableName("music")
                .withKeySchema(
                        new KeySchemaElement("title", KeyType.HASH),
                        new KeySchemaElement("artist", KeyType.RANGE)
                )
                .withAttributeDefinitions(
                        new AttributeDefinition("title", ScalarAttributeType.S),
                        new AttributeDefinition("artist", ScalarAttributeType.S),
                        new AttributeDefinition("year", ScalarAttributeType.N),
                        new AttributeDefinition("album", ScalarAttributeType.S)
                )
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName("YearAlbumIndex")
                                .withKeySchema(
                                        new KeySchemaElement("year", KeyType.HASH),
                                        new KeySchemaElement("album", KeyType.RANGE)
                                )
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                )
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        amazonDynamoDB.createTable(request);
        System.out.println("Created table: music");
    }

    /**
     * Creates the 'login' table with email as the primary key.
     */
    private void createLoginTable() {
        CreateTableRequest request = new CreateTableRequest()
                .withTableName("login")
                .withKeySchema(new KeySchemaElement("email", KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition("email", ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        amazonDynamoDB.createTable(request);
        System.out.println("Created table: login");
    }

    /**
     * Imports music data from 2025a1.json (inside resources) into the 'music' table.
     */
    private void importMusicData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        MusicWrapper wrapper = mapper.readValue(
                new ClassPathResource("2025a1.json").getInputStream(),
                MusicWrapper.class
        );

        List<Music> songs = wrapper.getSongs();
        songs.forEach(dynamoDBMapper::save);
        System.out.println("Imported " + songs.size() + " songs");
    }

    /**
     * Waits until a given table is in ACTIVE state before proceeding.
     */
    private void waitForTableToBecomeActive(String tableName) {
        System.out.println("Waiting for " + tableName + " to become ACTIVE...");
        boolean isActive = false;
        while (!isActive) {
            try {
                Thread.sleep(2000); // Poll every 2 seconds
                String status = amazonDynamoDB.describeTable(tableName).getTable().getTableStatus();
                if ("ACTIVE".equals(status)) {
                    isActive = true;
                    System.out.println("Table " + tableName + " is ACTIVE.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Adds 10 test users to the 'login' table with unique emails and usernames.
     */
    private void addTestUsers() {
        String passw = "0123456789";
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setEmail("s3945643" + i + "@student.rmit.edu.au");
            user.setUserName("ChristopherLamb" + i);

            // Generate a simple 6-digit password
            StringBuilder password = new StringBuilder();
            for (int j = 0; j < 6; j++) {
                password.append(passw.charAt((i + j) % passw.length()));
            }
            user.setPassword(password.toString());

            dynamoDBMapper.save(user);
        }
        System.out.println("Added 10 test users");
    }

    /**
     * Creates the 'subscriptions' table with user_id and title as keys if it does not already exist.
     */
    private void createSubscriptionsTable() {
        String tableName = "subscriptions";
        try {
            amazonDynamoDB.describeTable(tableName);
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

            amazonDynamoDB.createTable(request);
            System.out.println("Created table: " + tableName);
            waitForTableToBecomeActive(tableName);
        }
    }

    /**
     * Inserts two sample subscription records into the 'subscriptions' table.
     */
    private void insertSubscriptionTestData() {
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

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
