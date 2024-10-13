package com.instagram.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.instagram.dto.ProfileDTO;
import com.instagram.entity.Profile;
import com.instagram.mapper.ProfileMapper;
import com.instagram.repository.ProfileRepository;
import com.instagram.service.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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


    private List<ProfileDTO> loadFollowersFromMultipartFile(MultipartFile file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        // Parsiramo root element JSON fajla iz MultipartFile-a
        JsonNode rootNode = objectMapper.readTree(file.getInputStream());
        System.out.println("Učitani JSON: " + rootNode.toString());

        // Kreiramo listu za čuvanje vrednosti polja
        List<ProfileDTO> followers = new ArrayList<>();

        // Iteriramo kroz svaki objekat u JSON nizu
        if (rootNode.isArray()) {
            for (JsonNode node : rootNode) {
                // Iz svakog objekta uzimamo "string_list_data" polje
                JsonNode stringListDataNode = node.path("string_list_data");

                // Proveravamo da li "string_list_data" postoji i da li je niz
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
                    System.out.println("'string_list_data' nije pronađen ili nije niz.");
                }
            }
        } else {
            System.out.println("Root JSON nije niz.");
        }

        return followers;
    }

}
