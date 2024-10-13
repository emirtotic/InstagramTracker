package com.instagram.controller;

import com.instagram.dto.ProfileDTO;
import com.instagram.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instagram")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @PostMapping("/loadNewFollowersData")
    public ResponseEntity<String> loadNewFollowersData(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
        }

        List<ProfileDTO> profileDTOS = profileService.loadNewFollowersData(file);
        StringBuilder sb = new StringBuilder();
        profileDTOS.stream().forEach(profile -> sb.append(profile.getNickname()).append("\n"));

        return ResponseEntity.status(HttpStatus.CREATED).body(sb.toString().trim());
    }

    @PostMapping("/doesntFollowBack")
    public ResponseEntity<List<ProfileDTO>> doesntFollowBack(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<ProfileDTO> profileDTOS = profileService.doesntFollowBack(file);

        return ResponseEntity.status(HttpStatus.CREATED).body(profileDTOS);
    }
}
