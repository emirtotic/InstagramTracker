package com.instagram.repository;

import com.instagram.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Profile findByNickname(String nickname);

    void deleteByNickname(String nickname);
}
