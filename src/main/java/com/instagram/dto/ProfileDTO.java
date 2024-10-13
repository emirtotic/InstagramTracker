package com.instagram.dto;

import lombok.*;

import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProfileDTO {

    private String nickname;
    private String link;
    private Long timestamp;
    private Date createdAt;
    private Date modifiedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileDTO)) return false;
        ProfileDTO that = (ProfileDTO) o;
        return Objects.equals(getNickname(),
                that.getNickname()) && Objects.equals(getLink(),
                that.getLink()) && Objects.equals(getTimestamp(),
                that.getTimestamp());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNickname(), getLink(), getTimestamp());
    }
}
