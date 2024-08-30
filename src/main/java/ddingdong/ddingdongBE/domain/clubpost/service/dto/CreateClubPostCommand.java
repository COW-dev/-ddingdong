package ddingdong.ddingdongBE.domain.clubpost.service.dto;

import ddingdong.ddingdongBE.domain.club.entity.Club;
import ddingdong.ddingdongBE.domain.clubpost.entity.ClubPost;
import lombok.Builder;

@Builder
public record CreateClubPostCommand(
    Long userId,
    String activityContent,
    String fileName
) {

  public ClubPost toEntity(Club club, String fileUrl) {
    return ClubPost.builder()
        .activityContent(activityContent)
        .fileUrl(fileUrl)
        .club(club)
        .build();
  }
}
