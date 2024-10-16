package com.instagram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class InstagramTrackerConsole {

    public static void main(String[] args) {
        SpringApplication.run(InstagramTrackerConsole.class, args);

        try {
            loadFollowersFromFile();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadFollowersFromFile() throws Exception {

        String previousFollowersFile = "jsonFiles/newFollowersData.json";
        String currentFollowersFile = "jsonFiles/currentFollowers.json";
        String following = "jsonFiles/following.json";
        String pendingRequests = "jsonFiles/pending_follow_requests.json";

        List<String> previousFollowers = loadFollowersFromFile(previousFollowersFile);
        List<String> currentFollowers = loadFollowersFromFile(currentFollowersFile);
        List<String> followingList = loadFollowingUsersFromFile(following);
        List<String> loadPendingRequests = loadUnansweredFollowRequestsFromFile(pendingRequests);

        List<String> lostFollowers = lostFollowers(previousFollowers, currentFollowers);
        List<String> newFollowers = newFollowers(previousFollowers, currentFollowers);
        List<String> doesntFollowBack = whoDoesntFollowBack(currentFollowers, followingList);
        List<String> pendingRequestsList = pendingRequests(loadPendingRequests);

    }

    private static List<String> loadFollowersFromFile(String filePath) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            System.out.println("File found: " + filePath);

            JsonNode rootNode = objectMapper.readTree(inputStream);
            System.out.println("Loaded JSON: " + rootNode.toString());

            List<String> followerUsernames = new ArrayList<>();

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    JsonNode stringListDataNode = node.path("string_list_data");

                    if (stringListDataNode.isArray()) {
                        for (JsonNode followerNode : stringListDataNode) {
                            String username = followerNode.path("value").asText();
                            followerUsernames.add(username);
                        }
                    } else {
                        System.out.println("'string_list_data' is not found or it is not an array.");
                    }
                }
            } else {
                System.out.println("Root JSON is not array.");
            }

            return followerUsernames;
        }
    }

    private static List<String> loadFollowingUsersFromFile(String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            System.out.println("File opened: " + filePath);

            JsonNode rootNode = objectMapper.readTree(inputStream);
            System.out.println("Loaded JSON: " + rootNode.toString());

            List<String> followerUsernames = new ArrayList<>();

            JsonNode relationshipsFollowingNode = rootNode.path("relationships_following");

            if (relationshipsFollowingNode.isArray()) {
                for (JsonNode relationshipNode : relationshipsFollowingNode) {
                    JsonNode stringListDataNode = relationshipNode.path("string_list_data");

                    if (stringListDataNode.isArray()) {
                        for (JsonNode followerNode : stringListDataNode) {
                            String username = followerNode.path("value").asText();
                            followerUsernames.add(username);
                        }
                    } else {
                        System.out.println("'string_list_data' not found or it is not an array.");
                    }
                }
            } else {
                System.out.println("Polje 'relationships_following' not found or it is not an array.");
            }

            return followerUsernames;
        }
    }

    private static List<String> lostFollowers(List<String> root, List<String> fromLatestJson) {

        List<String> result = new ArrayList<>();

        for (String user : root) {
            if (!fromLatestJson.contains(user)) {
                result.add(user);
            }
        }

        if (result.isEmpty()) {
            System.out.println("No new unfollowers.");
        } else {
            System.out.println("Unfollowers:");
            result.forEach(System.out::println);
        }

        System.out.println("Total count of unfollowers: " + result.size());

        return result;
    }

    private static List<String> newFollowers(List<String> root, List<String> fromLatestJson) {

        List<String> result = new ArrayList<>();

        for (String user : fromLatestJson) {
            if (!root.contains(user)) {
                result.add(user);
            }
        }

        if (result.isEmpty()) {
            System.out.println("No new followers.");
        } else {
            System.out.println("New followers:");
            result.forEach(System.out::println);
        }

        System.out.println("Total new followers: " + result.size());

        return result;
    }

    private static List<String> whoDoesntFollowBack(List<String> followers, List<String> following) {

        List<String> result = new ArrayList<>();

        for (String user : following) {
            if (!followers.contains(user)) {
                result.add(user);
            }
        }

        if (result.isEmpty()) {
            System.out.println("All followers follow you.");
        } else {
            System.out.println("Doesn't follow back:");
            result.forEach(System.out::println);
        }

        System.out.println("Total: " + result.size());

        return result;
    }

    private static List<String> pendingRequests(List<String> pendingRequests) {

        if (pendingRequests.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            System.out.println("Pending requests:");
            pendingRequests.forEach(System.out::println);
        }

        System.out.println("Total pending requests: " + pendingRequests.size());

        return pendingRequests;
    }

    private static List<String> loadUnansweredFollowRequestsFromFile(String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            System.out.println("File loaded: " + filePath);

            JsonNode rootNode = objectMapper.readTree(inputStream);
            System.out.println("Loaded JSON: " + rootNode.toString());

            List<String> followRequestUsernames = new ArrayList<>();

            JsonNode followRequestsNode = rootNode.path("relationships_follow_requests_sent");

            if (followRequestsNode.isArray()) {
                for (JsonNode requestNode : followRequestsNode) {
                    JsonNode stringListDataNode = requestNode.path("string_list_data");

                    if (stringListDataNode.isArray()) {
                        for (JsonNode followerNode : stringListDataNode) {
                            String username = followerNode.path("value").asText();
                            followRequestUsernames.add(username);
                        }
                    } else {
                        System.out.println("'string_list_data' not found or it is not an array.");
                    }
                }
            } else {
                System.out.println("Field 'relationships_follow_requests_sent' not found or it is not an array.");
            }

            return followRequestUsernames;
        }
    }

}
