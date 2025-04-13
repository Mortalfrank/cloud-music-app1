package com.musicapp.controller;

import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.musicapp.model.Music;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.*;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import javax.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    private AmazonDynamoDB dynamoClient;

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        String currentUserEmail = getCurrentUserEmail(session);
        List<Music> subscriptions = fetchSubscriptions(currentUserEmail);
        model.addAttribute("subscriptions", subscriptions);
        return "home";
    }


    private List<Music> fetchSubscriptions(String userEmail) {
        System.out.println("[DEBUG] Start querying user subscriptions，email: " + userEmail);

        DynamoDB dynamoDB = new DynamoDB(dynamoClient);
        try {
            // 2. 打印subscriptions表查询条件
            Table subscriptionsTable = dynamoDB.getTable("subscriptions");
            QuerySpec querySpec = new QuerySpec()
                    .withKeyConditionExpression("user_id = :userId")
                    .withValueMap(new ValueMap().withString(":userId", userEmail));

            System.out.println("[DEBUG] 查询subscriptions表条件: " + querySpec.getKeyConditionExpression());

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