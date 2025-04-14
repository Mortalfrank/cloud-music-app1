package com.musicapp.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    public AmazonS3 amazonS3() {
        // Use the default credential supply chain, which automatically looks for credentials in environment variables
        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .build();
    }
}