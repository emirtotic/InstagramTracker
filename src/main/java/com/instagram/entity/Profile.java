package com.instagram.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(schema = "instagram-tracker", name = "profile")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nickname;
    private String link;
    private Long timestamp;
    @Column(name = "createdAt")
    private Date createdAt;
    @Column(name = "modifiedAt")
    private Date modifiedAt;
}
