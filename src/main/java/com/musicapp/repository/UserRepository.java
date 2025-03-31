package com.musicapp.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.musicapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public UserRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public User findByEmail(String email) {
        return dynamoDBMapper.load(User.class, email);
    }

    public void save(User user) {
        dynamoDBMapper.save(user);
    }
}