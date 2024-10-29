package ddingdong.ddingdongBE.domain.club.service;

import ddingdong.ddingdongBE.domain.club.entity.Club;
import ddingdong.ddingdongBE.domain.club.service.dto.command.UpdateClubInfoCommand;
import ddingdong.ddingdongBE.domain.club.service.dto.query.MyClubInfoQuery;
import ddingdong.ddingdongBE.domain.filemetadata.entity.DomainType;
import ddingdong.ddingdongBE.domain.filemetadata.service.FacadeFileMetaDataService;
import ddingdong.ddingdongBE.domain.filemetadata.service.dto.command.UpdateAllFileMetaDataCommand;
import ddingdong.ddingdongBE.domain.filemetadata.service.dto.query.FileMetaDataListQuery;
import ddingdong.ddingdongBE.file.service.S3FileService;
import ddingdong.ddingdongBE.file.service.dto.query.UploadedFileUrlQuery;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FacadeCentralClubServiceImpl implements FacadeCentralClubService {

    private final ClubService clubService;
    private final FacadeFileMetaDataService facadeFileMetaDataService;
    private final S3FileService s3FileService;

    @Override
    public MyClubInfoQuery getMyClubInfo(Long userId) {
        Club club = clubService.getByUserId(userId);
        String clubProfileImageKey = getFileKey(DomainType.CLUB_PROFILE, club.getId());
        String clubIntroductionImageKey = getFileKey(DomainType.CLUB_INTRODUCTION, club.getId());

        UploadedFileUrlQuery profileImageUrlQuery = s3FileService.getUploadedFileUrl(clubProfileImageKey);
        UploadedFileUrlQuery introductionImageUrlQuery = s3FileService.getUploadedFileUrl(clubIntroductionImageKey);
        return MyClubInfoQuery.of(club, profileImageUrlQuery, introductionImageUrlQuery);
    }

    @Override
    @Transactional
    public Long updateClubInfo(UpdateClubInfoCommand command) {
        Club club = clubService.getByUserId(command.userId());
        clubService.update(club, command.toEntity());
        updateFileMetaData(command.profileImageId(), DomainType.CLUB_PROFILE, club.getId());
        updateFileMetaData(command.introductionImageId(), DomainType.CLUB_INTRODUCTION, club.getId());
        return club.getId();
    }

    private String getFileKey(DomainType domainType, Long clubId) {
        return facadeFileMetaDataService.getAllByEntityTypeAndEntityId(domainType, clubId)
                .stream()
                .map(FileMetaDataListQuery::key)
                .findFirst()
                .orElse(null);
    }

    private void updateFileMetaData(String command, DomainType clubProfile, Long entityId) {
        facadeFileMetaDataService.updateAll(
                new UpdateAllFileMetaDataCommand(
                        Stream.of(command)
                                .filter(Objects::nonNull)
                                .toList(),
                        clubProfile,
                        entityId)
        );
    }

}
