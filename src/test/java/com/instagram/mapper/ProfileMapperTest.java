package com.instagram.mapper;

import com.instagram.dto.ProfileDTO;
import com.instagram.entity.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileMapperTest {

    private ProfileMapper profileMapper;

    @BeforeEach
    void setUp() {
        profileMapper = Mappers.getMapper(ProfileMapper.class);
    }

    @Test
    @DisplayName("Map ProfileDTO to Profile Test")
    void testMapToEntity() {
        ProfileDTO profileDTO = ProfileDTO.builder()
                .nickname("testuser")
                .link("http://link")
                .timestamp(123456789L)
                .createdAt(new Date())
                .modifiedAt(new Date())
                .build();

        Profile profile = profileMapper.mapToEntity(profileDTO);

        assertEquals(profileDTO.getNickname(), profile.getNickname());
        assertEquals(profileDTO.getLink(), profile.getLink());
        assertEquals(profileDTO.getTimestamp(), profile.getTimestamp());
    }

    @Test
    @DisplayName("Map ProfileDTO list to Profile list Test")
    void testMapToEntityList() {
        List<ProfileDTO> profileDTOList = new ArrayList<>();
        ProfileDTO profileDTO1 = ProfileDTO.builder()
                .nickname("user1")
                .link("http://link1")
                .timestamp(123L)
                .createdAt(new Date())
                .modifiedAt(new Date())
                .build();
        ProfileDTO profileDTO2 = ProfileDTO.builder()
                .nickname("user2")
                .link("http://link2")
                .timestamp(456L)
                .createdAt(new Date())
                .modifiedAt(new Date())
                .build();
        profileDTOList.add(profileDTO1);
        profileDTOList.add(profileDTO2);

        List<Profile> profileList = profileMapper.mapToEntity(profileDTOList);

        assertEquals(2, profileList.size());
        assertEquals(profileDTO1.getNickname(), profileList.get(0).getNickname());
        assertEquals(profileDTO2.getNickname(), profileList.get(1).getNickname());
    }

    @Test
    @DisplayName("Map Profile to ProfileDTO Test")
    void testMapToDTO() {
        Profile profile = new Profile();
        profile.setNickname("testuser");
        profile.setLink("http://link");
        profile.setTimestamp(123456789L);
        profile.setCreatedAt(new Date());
        profile.setModifiedAt(new Date());

        ProfileDTO profileDTO = profileMapper.mapToDTO(profile);

        assertEquals(profile.getNickname(), profileDTO.getNickname());
        assertEquals(profile.getLink(), profileDTO.getLink());
        assertEquals(profile.getTimestamp(), profileDTO.getTimestamp());
    }

    @Test
    @DisplayName("Map Profile list to ProfileDTO list Test")
    void testMapToDTOList() {
        List<Profile> profileList = new ArrayList<>();
        Profile profile1 = new Profile();
        profile1.setNickname("user1");
        profile1.setLink("http://link1");
        profile1.setTimestamp(123L);
        profile1.setCreatedAt(new Date());
        profile1.setModifiedAt(new Date());

        Profile profile2 = new Profile();
        profile2.setNickname("user2");
        profile2.setLink("http://link2");
        profile2.setTimestamp(456L);
        profile2.setCreatedAt(new Date());
        profile2.setModifiedAt(new Date());

        profileList.add(profile1);
        profileList.add(profile2);

        List<ProfileDTO> profileDTOList = profileMapper.mapToDTO(profileList);

        assertEquals(2, profileDTOList.size());
        assertEquals(profile1.getNickname(), profileDTOList.get(0).getNickname());
        assertEquals(profile2.getNickname(), profileDTOList.get(1).getNickname());
    }
}
