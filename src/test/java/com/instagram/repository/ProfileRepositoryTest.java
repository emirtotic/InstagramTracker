package com.instagram.repository;

import com.instagram.entity.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)  // Use H2 in-memory database
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    private Profile testProfile;

    @BeforeEach
    void setUp() {

        testProfile = Profile.builder()
                .nickname("testuser")
                .link("http://instagram.com/testuser")
                .timestamp(123456789L)
                .createdAt(new Date())
                .modifiedAt(new Date())
                .build();

        profileRepository.save(testProfile);
    }

    @Test
    @DisplayName("Find by nickname test")
    void testFindByNickname() {

        Profile foundProfile = profileRepository.findByNickname("testuser");

        assertNotNull(foundProfile);
        assertEquals("testuser", foundProfile.getNickname());
    }

    @Test
    @DisplayName("Find by nickname - not found")
    void testFindByNicknameNotFound() {

        Profile notFoundProfile = profileRepository.findByNickname("nonexistent");
        assertNull(notFoundProfile);
    }

    @Test
    @DisplayName("Delete by nickname test")
    void testDeleteByNickname() {

        Profile profileToDelete = profileRepository.findByNickname("testuser");
        assertNotNull(profileToDelete);

        profileRepository.deleteByNickname("testuser");

        Profile deletedProfile = profileRepository.findByNickname("testuser");
        assertNull(deletedProfile);
    }

    @Test
    @DisplayName("Delete by nickname not found test")
    void testDeleteByNicknameNonExist() {

        profileRepository.deleteByNickname("not exist");
        assertDoesNotThrow(() -> profileRepository.deleteByNickname("not exist"));
    }
}
