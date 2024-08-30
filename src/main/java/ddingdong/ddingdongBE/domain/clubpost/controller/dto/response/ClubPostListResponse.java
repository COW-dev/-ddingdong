package ddingdong.ddingdongBE.domain.clubpost.controller.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ClubPostListResponse(
    List<String> mediaUrls
) {

  public static ClubPostListResponse from(List<String> mediaUrls) {
    return ClubPostListResponse.builder()
        .mediaUrls(mediaUrls)
        .build();
  }
}