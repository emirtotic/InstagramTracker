package com.instagram.service.impl;

import com.instagram.dto.ProfileDTO;
import com.instagram.entity.Profile;
import com.instagram.mapper.ProfileMapper;
import com.instagram.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl profileService;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Load new followers list test")
    void testLoadNewFollowersDataSuccess() throws IOException {

        List<ProfileDTO> profileDTOs = new ArrayList<>();
        List<Profile> profiles = new ArrayList<>();

        when(profileMapper.mapToEntity(profileDTOs)).thenReturn(profiles);
        doNothing().when(profileRepository).deleteAll();
        when(profileRepository.saveAll(profiles)).thenReturn(profiles);
        when(multipartFile.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("followers.json"));

        List<ProfileDTO> result = profileService.loadNewFollowersData(new MockMultipartFile("profiles.json", new byte[0]));

        assertNotNull(result);
        verify(profileRepository, times(1)).deleteAll();
        verify(profileRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Load new followers throws exception test")
    void testLoadNewFollowersDataIOException() throws IOException {

        when(multipartFile.getInputStream()).thenThrow(IOException.class);

        assertThrows(RuntimeException.class, () -> profileService.loadNewFollowersData(multipartFile));

        verify(profileRepository, never()).deleteAll();
        verify(profileRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Doesnt follow back test")
    void testDoesntFollowBackSuccess() throws IOException {

        List<ProfileDTO> fromNewFile = new ArrayList<>();
        List<ProfileDTO> followers = new ArrayList<>();
        List<Profile> fromDB = new ArrayList<>();
        followers.add(new ProfileDTO("user1", "link1", 123L, null, null));
        fromNewFile.add(new ProfileDTO("user2", "link2", 123L, null, null));

        when(multipartFile.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("followers.json"));
        when(profileMapper.mapToDTO(fromDB)).thenReturn(followers);

        List<ProfileDTO> result = profileService.doesntFollowBack(new MockMultipartFile("profiles.json", new byte[0]));

        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getNickname());
    }

    @Test
    @DisplayName("Remove follower test")
    void testRemoveFollowerSuccess() {

        Profile profile = new Profile();
        profile.setNickname("user1");

        when(profileRepository.findByNickname("user1")).thenReturn(profile);

        profileService.removeFollower("user1");

        verify(profileRepository, times(1)).deleteByNickname("user1");
    }

    @Test
    @DisplayName("Remove follower not found test")
    void testRemoveFollowerUserNotFound() {

        when(profileRepository.findByNickname("user1")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> profileService.removeFollower("user1"));

        verify(profileRepository, never()).deleteByNickname("user1");
    }
}
