package com.instagram.mapper;

import com.instagram.dto.ProfileDTO;
import com.instagram.entity.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    @Mapping(target = "id", ignore = true)
    Profile mapToEntity(ProfileDTO profileDTO);

    List<Profile> mapToEntity(List<ProfileDTO> employeeDTOs);

    ProfileDTO mapToDTO(Profile profileDT);

    List<ProfileDTO> mapToDTO(List<Profile> employeeDTOs);

}
