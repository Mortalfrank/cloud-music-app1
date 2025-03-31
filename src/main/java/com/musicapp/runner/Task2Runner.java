package com.musicapp.runner;

import com.musicapp.service.ArtistImageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;

@Profile("task2") // This is executed only when the "task2" profile is enabled
//mvn spring-boot:run -Dspring-boot.run.profiles=task2
@Component
public class Task2Runner implements CommandLineRunner {
    private final ArtistImageService imageService;

    public Task2Runner(ArtistImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    public void run(String... args) throws Exception {
        imageService.processArtistImages("/2025a1.json");
    }
}