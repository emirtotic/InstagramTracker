package com.instagram.service;

import com.instagram.dto.ProfileDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProfileService {

    List<ProfileDTO> loadNewFollowersData(MultipartFile file);

    List<ProfileDTO> doesntFollowBack(MultipartFile file);

    List<ProfileDTO> sentRequests(MultipartFile file);
}
