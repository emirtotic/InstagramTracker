package com.instagram.controller;

import com.instagram.dto.ProfileDTO;
import com.instagram.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    @Mock
    private ProfileService profileService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ProfileController profileController;

    private MockMultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMultipartFile = new MockMultipartFile("file", "followers.json", "application/json", "{\"followers\":[]}".getBytes());
    }

    @Test
    @DisplayName("Load new followers data successful")
    void loadNewFollowersData_shouldReturnCreatedStatusAndSendKafkaMessage() {
        List<ProfileDTO> profileDTOS = new ArrayList<>();
        profileDTOS.add(new ProfileDTO("testuser", "http://link", 12345L, null, null));

        when(profileService.loadNewFollowersData(any(MultipartFile.class))).thenReturn(profileDTOS);

        ResponseEntity<String> response = profileController.loadNewFollowersData(mockMultipartFile);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("testuser", response.getBody());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("New followers data loaded.", kafkaCaptor.getValue());
    }

    @Test
    @DisplayName("Load new followers data unsuccessful")
    void loadNewFollowersData_shouldReturnBadRequestWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        ResponseEntity<String> response = profileController.loadNewFollowersData(emptyFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("No file uploaded", response.getBody());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("No followers data loaded.", kafkaCaptor.getValue());
    }

    @Test
    @DisplayName("Does not follow back data successful")
    void doesntFollowBack_shouldReturnOkStatusAndSendKafkaMessage() {
        List<ProfileDTO> profileDTOS = new ArrayList<>();
        profileDTOS.add(new ProfileDTO("testuser", "http://link", 12345L, null, null));

        when(profileService.doesntFollowBack(any(MultipartFile.class))).thenReturn(profileDTOS);

        ResponseEntity<List<ProfileDTO>> response = profileController.doesntFollowBack(mockMultipartFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("Unfollows list has been loaded.", kafkaCaptor.getValue());
    }

    @Test
    @DisplayName("Does not follow back data unsuccessful")
    void doesntFollowBack_shouldReturnBadRequestWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        ResponseEntity<List<ProfileDTO>> response = profileController.doesntFollowBack(emptyFile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("Error occurred while loading unfollows list.", kafkaCaptor.getValue());
    }

    @Test
    @DisplayName("Pending requests data successful")
    void sentRequests_shouldReturnOkStatusAndSendKafkaMessage() {
        List<ProfileDTO> profileDTOS = new ArrayList<>();
        profileDTOS.add(new ProfileDTO("testuser", "http://link", 12345L, null, null));

        when(profileService.sentRequests(any(MultipartFile.class))).thenReturn(profileDTOS);

        ResponseEntity<List<ProfileDTO>> response = profileController.sentRequests(mockMultipartFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("Pending requests loaded.", kafkaCaptor.getValue());
    }

    @Test
    @DisplayName("Pending requests data unsuccessful")
    void sentRequests_shouldReturnNoContentWhenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

        ResponseEntity<List<ProfileDTO>> response = profileController.sentRequests(emptyFile);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("Unsuccessful attempt to retrieve pending requests file.", kafkaCaptor.getValue());
    }

    @Test
    @DisplayName("Remove follower from DB successful")
    void removeFollower_shouldReturnOkStatusAndSendKafkaMessage() {
        ResponseEntity<String> response = profileController.removeFollower("testuser");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User testuser has been removed.", response.getBody());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("Follower testuser removed.", kafkaCaptor.getValue());
    }

    @Test
    @DisplayName("Remove follower from DB unsuccessful")
    void removeFollower_shouldReturnBadRequestWhenUsernameIsEmpty() {
        ResponseEntity<String> response = profileController.removeFollower("");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("User not found.", response.getBody());

        ArgumentCaptor<String> kafkaCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("followers-topic"), kafkaCaptor.capture());
        assertEquals("Unable to remove follower.", kafkaCaptor.getValue());
    }
}
