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

        // Search logic remains unchanged
        return "home";
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
}
