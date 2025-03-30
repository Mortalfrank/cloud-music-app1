package com.musicapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
//Artist table(title,artist,year,album,img_url)
public class Artist {
    private String title;
    private String artist;
    private String year;
    private String album;

    @JsonProperty("img_url")
    private String imageUrl;

}