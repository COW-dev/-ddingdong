package ddingdong.ddingdongBE.domain.fixzone.service;

import ddingdong.ddingdongBE.domain.club.entity.Club;
import ddingdong.ddingdongBE.domain.club.service.ClubService;
import ddingdong.ddingdongBE.domain.fixzone.entity.FixZone;
import ddingdong.ddingdongBE.domain.fixzone.entity.FixZoneComment;
import ddingdong.ddingdongBE.domain.fixzone.service.dto.command.CreateFixZoneCommentCommand;
import ddingdong.ddingdongBE.domain.fixzone.service.dto.command.UpdateFixZoneCommentCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FacadeAdminFixZoneCommentServiceImpl implements FacadeAdminFixZoneCommentService {

    private final FixZoneCommentService fixZoneCommentService;
    private final FixZoneService fixZoneService;
    private final ClubService clubService;

    @Override
    @Transactional
    public Long create(CreateFixZoneCommentCommand command) {
        Club adminClub = clubService.getByUserId(command.userId());
        FixZone fixZone = fixZoneService.getById(command.fixZoneId());
        return fixZoneCommentService.save(command.toEntity(adminClub, fixZone));
    }

    @Override
    @Transactional
    public Long update(UpdateFixZoneCommentCommand command) {
        FixZoneComment fixZoneComment = fixZoneCommentService.getById(command.fixZoneCommentId());
        fixZoneComment.update(command.toEntity());
        return fixZoneComment.getId();
    }

    @Override
    @Transactional
    public void delete(Long fixZoneCommentId) {
        fixZoneCommentService.delete(fixZoneCommentId);
    }
}
