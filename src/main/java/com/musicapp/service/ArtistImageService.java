package com.musicapp.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musicapp.model.Artist;
import com.musicapp.model.SongList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class ArtistImageService {
    private static final Logger logger = LoggerFactory.getLogger(ArtistImageService.class);

    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public ArtistImageService(AmazonS3 s3Client, ObjectMapper objectMapper) {
        this.s3Client = s3Client;
        this.objectMapper = objectMapper;
    }

    public void processArtistImages(String jsonFile) {
        try {
            // Parse using wrapper classes
            SongList songList = objectMapper.readValue(
                    new ClassPathResource(jsonFile).getInputStream(),
                    SongList.class);

            // Process the songs list
            songList.getSongs().forEach(artist -> {
                if (artist.getImageUrl() != null) {
                    try {
                        uploadToS3(artist);
                    } catch (Exception e) {
                        logger.error("Failed to upload image for artist: {}", artist.getArtist(), e);
                    }
                }
            });
        } catch (IOException e) {
            logger.error("Failed to read JSON file", e);
            throw new RuntimeException("JSON processing failed", e);
        }
    }

    private void uploadToS3(Artist artist) throws IOException {
        String fileExtension = artist.getImageUrl().substring(artist.getImageUrl().lastIndexOf('.'));
        String s3Key = "artist-images/" + sanitizeName(artist.getArtist()) + fileExtension;

        // Check whether the file already exists
        if (s3Client.doesObjectExist(bucketName, s3Key)) {
            logger.info("File already exists in S3: {}, skipping upload", s3Key);
            return;
        }

        // Gets the file size and content type
        URL url = new URL(artist.getImageUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD"); // Only get the header information, do not download the content

        long contentLength = connection.getContentLengthLong();
        String contentType = connection.getContentType();

        // If the HEAD request cannot GET the size, try the GET request
        if (contentLength <= 0) {
            connection.disconnect();
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            contentLength = connection.getContentLengthLong();
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType != null ? contentType : getContentType(fileExtension));

        try (InputStream imageStream = url.openStream()) {
            s3Client.putObject(bucketName, s3Key, imageStream, metadata);
            logger.info("Successfully uploaded {} to S3", s3Key);
        } catch (Exception e) {
            logger.error("S3 upload failed for artist: {}", artist.getArtist(), e);
            throw new IOException("S3 upload failed", e);
        } finally {
            connection.disconnect();
        }
    }

    private String sanitizeName(String name) {
        return name.replaceAll("[^a-zA-Z0-9-]", "-");
    }


    private String getContentType(String extension) {
        switch (extension.toLowerCase()) {
            case ".jpg": case ".jpeg": return "image/jpeg";
            case ".png": return "image/png";
            case ".gif": return "image/gif";
            default: return "application/octet-stream";
        }
   }
}
