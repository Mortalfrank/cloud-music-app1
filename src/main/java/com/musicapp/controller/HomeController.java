package com.musicapp.controller;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.musicapp.model.Music;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import java.util.ArrayList;

@Controller
public class HomeController {
    @Autowired
    private AmazonDynamoDB dynamoClient;

    @GetMapping("/")
    public String home(Model model) {
        List<Music> subscriptions = fetchSubscriptions();  // Fetch subscription data
        model.addAttribute("subscriptions", subscriptions);  // Add data to the model
        return "home";  // Return the view
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String album,
            Model model) {

        List<Music> results = searchMusic(title, artist, year, album); // Perform search with the given params
        model.addAttribute("results", results);  // Add search results to the model
        return "home";  // Return the view with search results
    }

    // Method to fetch subscription data
    private List<Music> fetchSubscriptions() {
        List<Music> subscriptions = new ArrayList<>();

        // Create a DynamoDB client
        DynamoDB dynamoDB = new DynamoDB(dynamoClient);
        Table table = dynamoDB.getTable("music");  // Assuming your DynamoDB table name is "music"

        // Execute scan operation to fetch all records
        ScanSpec scanSpec = new ScanSpec();

        // Execute query and retrieve data
        try {
            // Process DynamoDB data mapping and populate Music objects
            Iterable<Item> items = table.scan(scanSpec);  // Perform scan to retrieve items

            // Iterate and convert to Music objects
            for (Item item : items) {
                Music music = new Music();
                music.setTitle(item.getString("title"));
                music.setArtist(item.getString("artist"));
                music.setYear(item.getInt("year"));
                music.setAlbum(item.getString("album"));
                music.setImg_url(item.getString("img_url"));

                subscriptions.add(music);  // Add to the list
            }
        } catch (Exception e) {
            e.printStackTrace();  // Exception handling
        }

        return subscriptions;
    }

    // Method to fetch search query results
    private List<Music> searchMusic(String title, String artist, Integer year, String album) {
        List<Music> musicList = new ArrayList<>();

        // Create a DynamoDB client
        DynamoDB dynamoDB = new DynamoDB(dynamoClient);
        Table table = dynamoDB.getTable("music");  // Assuming your DynamoDB table name is "music"

        // Start building a ScanSpec with filters
        ScanSpecBuilder scanSpecBuilder = new ScanSpecBuilder();

        // Add filters dynamically based on parameters
        if (title != null && !title.isEmpty()) {
            scanSpecBuilder.withFilterExpression("title = :title")
                    .withValueMap(new ValueMap().withString(":title", title));
        }
        if (artist != null && !artist.isEmpty()) {
            scanSpecBuilder.withFilterExpression("artist = :artist")
                    .withValueMap(new ValueMap().withString(":artist", artist));
        }
        if (year != null) {
            scanSpecBuilder.withFilterExpression("year = :year")
                    .withValueMap(new ValueMap().withNumber(":year", year));
        }
        if (album != null && !album.isEmpty()) {
            scanSpecBuilder.withFilterExpression("album = :album")
                    .withValueMap(new ValueMap().withString(":album", album));
        }

        // Create a ScanSpec with the dynamic filters
        ScanSpec scanSpec = scanSpecBuilder.build();

        try {
            // Execute scan with dynamic filters
            Iterable<Item> items = table.scan(scanSpec);

            // Process DynamoDB data mapping and populate Music objects
            for (Item item : items) {
                Music music = new Music();
                music.setTitle(item.getString("title"));
                music.setArtist(item.getString("artist"));
                music.setYear(item.getInt("year"));
                music.setAlbum(item.getString("album"));
                music.setImg_url(item.getString("img_url"));

                musicList.add(music);  // Add to the list
            }
        } catch (Exception e) {
            e.printStackTrace();  // Exception handling
        }

        return musicList;
    }

}
