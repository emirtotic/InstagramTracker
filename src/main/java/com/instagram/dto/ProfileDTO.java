package com.instagram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProfileDTO {

    private String nickname;
    private String link;
    private Long timestamp;
    private Date createdAt;
    private Date modifiedAt;
}
