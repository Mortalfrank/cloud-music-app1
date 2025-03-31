package com.musicapp.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "login")
public class User {
    private String email;
    private String userName;
    private String password;

    @DynamoDBHashKey(attributeName = "email")
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @DynamoDBAttribute(attributeName = "user_name")
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    @DynamoDBAttribute(attributeName = "password")
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}