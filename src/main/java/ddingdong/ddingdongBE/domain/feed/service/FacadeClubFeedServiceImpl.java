package ddingdong.ddingdongBE.domain.feed.service;

import ddingdong.ddingdongBE.domain.club.entity.Club;
import ddingdong.ddingdongBE.domain.club.service.ClubService;
import ddingdong.ddingdongBE.domain.feed.entity.Feed;
import ddingdong.ddingdongBE.domain.feed.repository.FeedRepository;
import ddingdong.ddingdongBE.domain.feed.service.dto.command.CreateFeedCommand;
import ddingdong.ddingdongBE.domain.filemetadata.entity.DomainType;
import ddingdong.ddingdongBE.domain.filemetadata.service.FileMetaDataService;
import ddingdong.ddingdongBE.file.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FacadeClubFeedServiceImpl implements FacadeClubFeedService{

    private final FeedRepository feedRepository;
    private final ClubService clubService;
    private final FileMetaDataService fileMetaDataService;
    private final S3FileService s3FileService;
    private final FeedService feedService;

    @Transactional
    @Override
    public void create(CreateFeedCommand command) {
        Club club = clubService.getByUserId(command.user().getId());
        Feed feed = command.toEntity(club);
        Long createdId = feedService.create(feed);

        if (feed.isImage()) {
            fileMetaDataService.updateStatusToCoupled(command.mediaId(), DomainType.FEED_IMAGE, createdId);
        }

        if (feed.isVideo()) {
            fileMetaDataService.updateStatusToCoupled(command.mediaId(), DomainType.FEED_VIDEO, createdId);
        }
    }
}