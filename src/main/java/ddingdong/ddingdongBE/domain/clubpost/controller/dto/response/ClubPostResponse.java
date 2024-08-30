package ddingdong.ddingdongBE.domain.clubpost.controller.dto.response;

import ddingdong.ddingdongBE.domain.clubpost.entity.ClubPost;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ClubPostResponse(
    String activityContent,
    String fileUrl,
    String clubName,
    LocalDate createdDate
) {

  public static ClubPostResponse from(ClubPost clubPost) {
    return ClubPostResponse.builder()
        .activityContent(clubPost.getActivityContent())
        .fileUrl(clubPost.getFileUrl())
        .clubName(clubPost.getClub().getName())
        .createdDate(LocalDate.from(clubPost.getCreatedAt()))
        .build();
  }
}
