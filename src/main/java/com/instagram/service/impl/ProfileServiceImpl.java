//package com.instagram.service.impl;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.instagram.repository.ProfileRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class ProfileServiceImpl {
//
//    @Autowired
//    private ProfileRepository profileRepository;
//
//
//    public void loadFollowersFromFile() throws Exception {
//
//        String previousFollowersFile = "previous_followers.json"; // ubaci putanje
//        String currentFollowersFile = "current_followers.json";
//
//        List<String> previousFollowers = loadFollowersFromFile(previousFollowersFile);
//        List<String> currentFollowers = loadFollowersFromFile(currentFollowersFile);
//
//        previousFollowers.removeAll(currentFollowers);
//
//        if (previousFollowers.isEmpty()) {
//            System.out.println("Niko vas nije otpratio.");
//        } else {
//            System.out.println("Sledeći nalozi su vas otpratili:");
//            previousFollowers.forEach(System.out::println);
//        }
//
//    }
//
//    private static List<String> loadFollowersFromFile(String filePath) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        byte[] jsonData = Files.readAllBytes(Paths.get(filePath));
//
//        // Parsiramo listu objekata koji sadrže više polja (username, full_name, itd.)
//        List<Map<String, Object>> followersList = objectMapper.readValue(jsonData, new TypeReference<List<Map<String, Object>>>() {});
//
//        // Izvlačimo samo vrednosti iz "username" polja
//        return followersList.stream()
//                .map(follower -> (String) follower.get("username"))
//                .toList();
//    }
//
//
//}
