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
            System.out.println("Fajl uspešno otvoren: " + filePath);

            JsonNode rootNode = objectMapper.readTree(inputStream);
            System.out.println("Učitani JSON: " + rootNode.toString());

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
                        System.out.println("'string_list_data' nije pronađen ili nije niz.");
                    }
                }
            } else {
                System.out.println("Root JSON nije niz.");
            }

            return followerUsernames;
        }
    }

    private static List<String> loadFollowingUsersFromFile(String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            System.out.println("Fajl uspesno otvoren: " + filePath);

            JsonNode rootNode = objectMapper.readTree(inputStream);
            System.out.println("Ucitani JSON: " + rootNode.toString());

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
                        System.out.println("'string_list_data' nije pronađen ili nije niz.");
                    }
                }
            } else {
                System.out.println("Polje 'relationships_following' nije niz ili nije pronađeno.");
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
            System.out.println("Niko vas nije otpratio.");
        } else {
            System.out.println("Sledeći nalozi su vas otpratili:");
            result.forEach(System.out::println);
        }

        System.out.println("Ukupan broj ljudi koji su vas otpratili: " + result.size());

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
            System.out.println("Niko vas nije zapratio.");
        } else {
            System.out.println("Sledeći nalozi su vas zapratili:");
            result.forEach(System.out::println);
        }

        System.out.println("Broj ljudi koji vas je zapratio: " + result.size());

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
            System.out.println("Svi koje pratis, prate i tebe.");
        } else {
            System.out.println("Sledeći nalozi vas ne prate a vi ih pratite:");
            result.forEach(System.out::println);
        }

        System.out.println("Ukupan broj ljudi koji vas ne prate: " + result.size());

        return result;
    }

    private static List<String> pendingRequests(List<String> pendingRequests) {

        if (pendingRequests.isEmpty()) {
            System.out.println("Nema neodgovorenih zahteva.");
        } else {
            System.out.println("Sledeći nalozi nisu prihvatili da ih pratite:");
            pendingRequests.forEach(System.out::println);
        }

        System.out.println("Ukupan broj ljudi koji nisu prihvatili pracenje: " + pendingRequests.size());

        return pendingRequests;
    }

    private static List<String> loadUnansweredFollowRequestsFromFile(String filePath) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        ClassPathResource resource = new ClassPathResource(filePath);
        try (InputStream inputStream = resource.getInputStream()) {
            System.out.println("Fajl uspešno otvoren: " + filePath);

            JsonNode rootNode = objectMapper.readTree(inputStream);
            System.out.println("Učitani JSON: " + rootNode.toString());

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
                        System.out.println("'string_list_data' nije pronađen ili nije niz.");
                    }
                }
            } else {
                System.out.println("Polje 'relationships_follow_requests_sent' nije niz ili nije pronađeno.");
            }

            return followRequestUsernames;
        }
    }

}
