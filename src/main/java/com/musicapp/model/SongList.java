package com.musicapp.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SongList {
    @JsonProperty("songs")
    private List<Artist> songs;
}