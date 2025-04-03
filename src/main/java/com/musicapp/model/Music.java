package com.musicapp.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

@Data
@DynamoDBTable(tableName = "music")
public class Music {
    @DynamoDBHashKey
    private String title;

    @DynamoDBAttribute
    private String artist;

    @DynamoDBAttribute
    private Integer year;

    @DynamoDBAttribute
    private String album;

    @DynamoDBAttribute
    private String img_url;
}