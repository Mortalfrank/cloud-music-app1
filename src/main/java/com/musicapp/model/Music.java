package com.musicapp.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.Data;

@DynamoDBTable(tableName = "music")
@Data
public class Music {
    @DynamoDBHashKey
    private String title;

    @DynamoDBRangeKey
    private String artist;

    @DynamoDBAttribute
    private Integer year;

    @DynamoDBAttribute
    private String album;

    @DynamoDBAttribute
    private String img_url;
}

