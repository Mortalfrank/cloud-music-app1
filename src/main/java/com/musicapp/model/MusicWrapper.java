package com.musicapp.model;

import lombok.Data;

import java.util.List;

@Data
public class MusicWrapper {
    private List<Music> songs;
}
