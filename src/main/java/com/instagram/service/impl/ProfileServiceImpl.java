package com.instagram.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.dto.ProfileDTO;
import com.instagram.entity.Profile;
import com.instagram.mapper.ProfileMapper;
import com.instagram.repository.ProfileRepository;
import com.instagram.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;

    @Override
    public List<ProfileDTO> loadNewFollowersData(MultipartFile file) {

        List<ProfileDTO> followers;

        try {
            log.info("Refreshing your followers list in progress...");
            followers = loadFollowersFromMultipartFile(file);
            List<Profile> profiles = profileMapper.mapToEntity(followers);
            profileRepository.deleteAll();
            profileRepository.saveAll(profiles);
            log.info("Your followers list has been refreshed.");

        } catch (IOException e) {
            log.error("Error occurred while refreshing your followers list.");
            throw new RuntimeException(e);
        }

        return followers;
    }

    @Override
    public List<ProfileDTO> doesntFollowBack(MultipartFile file) {

        List<ProfileDTO> fromNewFile;

        try {
            fromNewFile = loadFollowersFromMultipartFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<ProfileDTO> followers = profileMapper.mapToDTO(profileRepository.findAll());

        return followers.stream()
                .filter(user -> !fromNewFile.contains(user))
                .collect(Collectors.toList());

    }

    @Override
    public List<ProfileDTO> sentRequests(MultipartFile file) {
        try {
            return loadUnansweredFollowRequestsFromFile(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional
    public void removeFollower(String username) {

        Optional<Profile> profile = Optional.ofNullable(profileRepository.findByNickname(username));

        if (profile.isPresent()) {
            profileRepository.deleteByNickname(username);
            log.info("User " + profile.get().getNickname() + " has been removed from DB.");
        } else {
            throw new RuntimeException("User with name " + username + " is not found in database.");
        }
    }

    private List<ProfileDTO> loadFollowersFromMultipartFile(MultipartFile file) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        log.info("Parsing root element from JSON file from MultipartFile");

        JsonNode rootNode = objectMapper.readTree(file.getInputStream());
        log.info("Loaded JSON: " + rootNode.toString());

        List<ProfileDTO> followers = new ArrayList<>();

        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                // retrieve "string_list_data" field
                JsonNode stringListDataNode = node.path("string_list_data");

                // Check does "string_list_data" exist and is it array
                if (stringListDataNode.isArray()) {
                    for (JsonNode followerNode : stringListDataNode) {
                        String username = followerNode.path("value").asText();
                        String href = followerNode.path("href").asText();
                        Long timestamp = followerNode.path("timestamp").asLong();
                        ProfileDTO profileDTO = ProfileDTO.builder()
                                .nickname(username)
                                .link(href)
                                .timestamp(timestamp)
                                .createdAt(new Date())
                                .modifiedAt(new Date())
                                .build();
                        followers.add(profileDTO);
                    }
                } else {
                    log.error("'string_list_data' not found.");
                }
            }
        } else {
            log.error("Root JSON is not array.");
        }

        return followers;
    }

    private static List<ProfileDTO> loadUnansweredFollowRequestsFromFile(MultipartFile file) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        // Parsing of root element in JSON
        JsonNode rootNode = objectMapper.readTree(file.getInputStream());
        System.out.println("Loaded JSON: " + rootNode.toString());

        List<ProfileDTO> followRequests = new ArrayList<>();

        JsonNode followRequestsNode = rootNode.path("relationships_follow_requests_sent");

        if (followRequestsNode.isArray()) {
            for (JsonNode requestNode : followRequestsNode) {

                JsonNode stringListDataNode = requestNode.path("string_list_data");

                if (stringListDataNode.isArray()) {
                    for (JsonNode followerNode : stringListDataNode) {
                        String username = followerNode.path("value").asText();
                        String href = followerNode.path("href").asText();
                        Long timestamp = followerNode.path("timestamp").asLong();
                        ProfileDTO profileDTO = ProfileDTO.builder()
                                .nickname(username)
                                .link(href)
                                .timestamp(timestamp)
                                .createdAt(new Date())
                                .modifiedAt(new Date())
                                .build();
                        followRequests.add(profileDTO);
                    }
                } else {
                    System.out.println("'string_list_data' is not found or it is not an array.");
                }
            }
        } else {
            System.out.println("Field 'relationships_follow_requests_sent' is not found.");
        }

        return followRequests;

    }

}
