package com.instagram.controller;

import com.instagram.dto.ProfileDTO;
import com.instagram.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "Instagram Profile Management", description = "Operations related to Instagram follower management")
@RequestMapping("/api/v1/instagram")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Operation(summary = "Load all followers", description = "Uploads a file with new follower data and returns a list of profiles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Followers data successfully uploaded",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid file input", content = @Content)
    })
    @PostMapping(value = "/loadNewFollowersData", consumes = "multipart/form-data", produces = "text/plain")
    public ResponseEntity<String> loadNewFollowersData(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            kafkaTemplate.send("followers-topic", "No followers data loaded.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
        }

        List<ProfileDTO> profileDTOS = profileService.loadNewFollowersData(file);
        StringBuilder sb = new StringBuilder();
        profileDTOS.stream().forEach(profile -> sb.append(profile.getNickname()).append("\n"));

        kafkaTemplate.send("followers-topic", "New followers data loaded.");

        return ResponseEntity.status(HttpStatus.CREATED).body(sb.toString().trim());
    }

    @Operation(summary = "Find users who don't follow back", description = "Uploads a file with follower data and returns a list of users who don't follow back")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file input", content = @Content)
    })
    @PostMapping(value = "/doesntFollowBack", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<List<ProfileDTO>> doesntFollowBack(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            kafkaTemplate.send("followers-topic", "Error occurred while loading unfollows list.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<ProfileDTO> profileDTOS = profileService.doesntFollowBack(file);
        kafkaTemplate.send("followers-topic", "Unfollows list has been loaded.");
        return ResponseEntity.status(HttpStatus.OK).body(profileDTOS);
    }

    @Operation(summary = "Get list of sent follow requests", description = "Uploads a file with follower data and returns a list of sent follow requests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDTO.class))),
            @ApiResponse(responseCode = "204", description = "No content", content = @Content)
    })
    @PostMapping(value = "/sentRequests", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<List<ProfileDTO>> sentRequests(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            kafkaTemplate.send("followers-topic", "Unsuccessful attempt to retrieve pending requests file.");
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }

        List<ProfileDTO> profileDTOS = profileService.sentRequests(file);
        kafkaTemplate.send("followers-topic", "Pending requests loaded.");
        return ResponseEntity.status(HttpStatus.OK).body(profileDTOS);
    }

    @Operation(summary = "Remove a follower", description = "Removes a follower by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Follower removed successfully", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid username provided", content = @Content)
    })
    @DeleteMapping("/removeFollower/{username}")
    public ResponseEntity<String> removeFollower(@PathVariable("username") String username) {

        if (username.isEmpty()) {
            kafkaTemplate.send("followers-topic", "Unable to remove follower.");
            return new ResponseEntity<>("User not found.", HttpStatus.BAD_REQUEST);
        }

        profileService.removeFollower(username);
        kafkaTemplate.send("followers-topic", "Follower " + username + " removed.");
        return ResponseEntity.status(HttpStatus.OK).body("User " + username + " has been removed.");
    }
}
