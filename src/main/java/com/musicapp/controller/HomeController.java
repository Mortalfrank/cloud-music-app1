package com.musicapp.controller;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.musicapp.model.Music;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    private AmazonDynamoDB dynamoClient;

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String album,
            Model model,
            HttpSession session) {

        String currentUserEmail = getCurrentUserEmail(session);
        List<Music> subscriptions = fetchSubscriptions(currentUserEmail);
        model.addAttribute("subscriptions", subscriptions);

        if (title != null || artist != null || album != null || year != null) {
            List<Music> results = searchSongs(title, year, artist, album);
            model.addAttribute("results", results);
        }

        return "home";
    }

    private List<Music> searchSongs(String title, Integer year, String artist, String album) {
        DynamoDB dynamoDB = new DynamoDB(dynamoClient);
        Table musicTable = dynamoDB.getTable("music");

        ScanSpec scanSpec = new ScanSpec();
        StringBuilder filterExpr = new StringBuilder();
        ValueMap valueMap = new ValueMap();
        Map<String, String> nameMap = new HashMap<>();

        nameMap.put("#yearAttr", "year");

        if (title != null && !title.isEmpty()) {
            filterExpr.append("contains(title, :title)");
            valueMap.withString(":title", title);
        }
        if (artist != null && !artist.isEmpty()) {
            if (filterExpr.length() > 0) filterExpr.append(" and ");
            filterExpr.append("contains(artist, :artist)");
            valueMap.withString(":artist", artist);
        }
        if (album != null && !album.isEmpty()) {
            if (filterExpr.length() > 0) filterExpr.append(" and ");
            filterExpr.append("contains(album, :album)");
            valueMap.withString(":album", album);
        }
        if (year != null) {
            if (filterExpr.length() > 0) filterExpr.append(" and ");
            filterExpr.append("#yearAttr = :year");
            valueMap.withInt(":year", year);
        }

        if (filterExpr.length() == 0) {
            return Collections.emptyList();
        }

        scanSpec
                .withFilterExpression(filterExpr.toString())
                .withValueMap(valueMap)
                .withNameMap(nameMap);

        List<Music> results = new ArrayList<>();
        try {
            ItemCollection<ScanOutcome> items = musicTable.scan(scanSpec);
            for (Item item : items) {
                Music music = new Music();
                music.setTitle(item.getString("title"));
                music.setArtist(item.getString("artist"));
                music.setYear(item.getInt("year"));
                music.setAlbum(item.getString("album"));
                music.setImg_url(item.getString("img_url"));
                results.add(music);
            }
        } catch (Exception e) {
            System.err.println("Error searching songs: " + e.getMessage());
            e.printStackTrace();
        }
        return results;
    }

    private List<Music> fetchSubscriptions(String userEmail) {
        System.out.println("[DEBUG] Start querying user subscriptions，email: " + userEmail);

        DynamoDB dynamoDB = new DynamoDB(dynamoClient);
        try {
            Table subscriptionsTable = dynamoDB.getTable("subscriptions");
            QuerySpec querySpec = new QuerySpec()
                    .withKeyConditionExpression("user_id = :userId")
                    .withValueMap(new ValueMap().withString(":userId", userEmail));

            System.out.println("[DEBUG] Query conditions for the subscriptions table : " + querySpec.getKeyConditionExpression());

            int subscriptionCount = 0;
            Set<String> subscribedTitles = new HashSet<>();
            for (Item subscription : subscriptionsTable.query(querySpec)) {
                String title = subscription.getString("title");
                String artist = subscription.getString("artist");
                subscribedTitles.add(title + "|" + artist);
                subscriptionCount++;
                System.out.println("[DEBUG] Find a subscription record: title=" + title + ", artist=" + artist);
            }
            System.out.println("[DEBUG] Found" + subscriptionCount + " Subscription record");

            Table musicTable = dynamoDB.getTable("music");
            List<Music> subscriptions = new ArrayList<>();
            for (String key : subscribedTitles) {
                String[] parts = key.split("\\|");
                String title = parts[0];
                String artist = parts[1];

                Item musicItem = musicTable.getItem("title", title, "artist", artist);
                if (musicItem == null) {
                    System.err.println("[ERROR] Music records unFound: title=" + title + ", artist=" + artist);
                } else {
                    System.out.println("[DEBUG] Find music records: " + musicItem.toJSON());
                    Music music = new Music();
                    music.setTitle(musicItem.getString("title"));
                    music.setArtist(musicItem.getString("artist"));
                    music.setYear(musicItem.getInt("year"));
                    music.setAlbum(musicItem.getString("album"));
                    music.setImg_url(musicItem.getString("img_url"));
                    subscriptions.add(music);
                }
            }
            return subscriptions;
        } catch (Exception e) {
            System.err.println("[ERROR] Error querying subscription:");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Use Session instead of Spring Security to get the current user
    private String getCurrentUserEmail(HttpSession session) {
        Object emailObj = session.getAttribute("userEmail");
        System.out.println("[DEBUG] session email attribute: " + emailObj);
        return emailObj != null ? emailObj.toString() : null;
    }

}