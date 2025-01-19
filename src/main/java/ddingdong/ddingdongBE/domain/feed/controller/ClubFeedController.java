package ddingdong.ddingdongBE.domain.feed.controller;

import ddingdong.ddingdongBE.auth.PrincipalDetails;
import ddingdong.ddingdongBE.domain.feed.api.ClubFeedApi;
import ddingdong.ddingdongBE.domain.feed.controller.dto.request.CreateFeedRequest;
import ddingdong.ddingdongBE.domain.feed.controller.dto.request.UpdateFeedRequest;
import ddingdong.ddingdongBE.domain.feed.controller.dto.response.MyFeedPageResponse;
import ddingdong.ddingdongBE.domain.feed.service.FacadeClubFeedService;
import ddingdong.ddingdongBE.domain.feed.service.dto.query.MyFeedPageQuery;
import ddingdong.ddingdongBE.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClubFeedController implements ClubFeedApi {

    private final FacadeClubFeedService facadeClubFeedService;

    @Override
    public void createFeed(
        CreateFeedRequest createFeedRequest,
        PrincipalDetails principalDetails
    ) {
        User user = principalDetails.getUser();
        facadeClubFeedService.create(createFeedRequest.toCommand(user));
    }

    @Override
    public void updateFeed(
        Long feedId,
        UpdateFeedRequest updateFeedRequest
    ) {
        facadeClubFeedService.update(updateFeedRequest.toCommand(feedId));
    }

    @Override
    public void deleteFeed(Long feedId) {
        facadeClubFeedService.delete(feedId);
    }

    @Override
    public MyFeedPageResponse getMyFeedPage(PrincipalDetails principalDetails, int size, Long currentCursorId) {
        User user = principalDetails.getUser();
        MyFeedPageQuery query = facadeClubFeedService.getMyFeedPage(user, size, currentCursorId);
        return MyFeedPageResponse.from(query);
    }
}
