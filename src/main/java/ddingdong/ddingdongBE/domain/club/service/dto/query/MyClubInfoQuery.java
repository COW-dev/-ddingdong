package ddingdong.ddingdongBE.domain.club.service.dto.query;

import ddingdong.ddingdongBE.domain.club.entity.Club;
import ddingdong.ddingdongBE.domain.clubmember.entity.ClubMember;
import java.time.LocalDateTime;
import java.util.List;

public record MyClubInfoQuery(
        String name,
        String category,
        String tag,
        String content,
        String leader,
        String phoneNumber,
        String location,
        LocalDateTime startRecruitPeriod,
        LocalDateTime endRecruitPeriod,
        String regularMeeting,
        String introduction,
        String activity,
        String ideal,
        String formUrl,
        List<String> profileImageUrls,
        List<String> introduceImageUrls,
        List<ClubMember> clubMembers
) {

    public static MyClubInfoQuery of(Club club, List<String> profileImageUrls, List<String> introduceImageUrls) {
        return new MyClubInfoQuery(
                club.getName(),
                club.getCategory(),
                club.getTag(),
                club.getContent(),
                club.getLeader(),
                club.getPhoneNumber().getNumber(),
                club.getLocation().getValue(),
                club.getStartRecruitPeriod(),
                club.getEndRecruitPeriod(),
                club.getRegularMeeting(),
                club.getIntroduction(),
                club.getActivity(),
                club.getIdeal(),
                club.getFormUrl(),
                profileImageUrls,
                introduceImageUrls,
                club.getClubMembers()
        );
    }
}