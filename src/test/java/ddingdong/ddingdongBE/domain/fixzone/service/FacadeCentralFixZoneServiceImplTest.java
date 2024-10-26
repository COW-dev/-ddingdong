package ddingdong.ddingdongBE.domain.fixzone.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.navercorp.fixturemonkey.FixtureMonkey;
import ddingdong.ddingdongBE.common.support.FixtureMonkeyFactory;
import ddingdong.ddingdongBE.common.support.TestContainerSupport;
import ddingdong.ddingdongBE.domain.club.entity.Club;
import ddingdong.ddingdongBE.domain.club.repository.ClubRepository;
import ddingdong.ddingdongBE.domain.fixzone.entity.FixZone;
import ddingdong.ddingdongBE.domain.fixzone.repository.FixZoneRepository;
import ddingdong.ddingdongBE.domain.fixzone.service.dto.command.CreateFixZoneCommand;
import ddingdong.ddingdongBE.domain.fixzone.service.dto.command.UpdateFixZoneCommand;
import ddingdong.ddingdongBE.domain.fixzone.service.dto.query.CentralMyFixZoneListQuery;
import ddingdong.ddingdongBE.domain.scorehistory.entity.Score;
import ddingdong.ddingdongBE.domain.user.entity.User;
import ddingdong.ddingdongBE.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FacadeCentralFixZoneServiceImplTest extends TestContainerSupport {

    @Autowired
    private FacadeCentralFixZoneService facadeCentralFixZoneService;
    @Autowired
    private FixZoneRepository fixZoneRepository;
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private UserRepository userRepository;

    private final FixtureMonkey fixture = FixtureMonkeyFactory.getNotNullBuilderIntrospectorMonkey();

    @DisplayName("동아리 - 픽스존 생성")
    @Test
    void create() {
        //given
        User user = fixture.giveMeOne(User.class);
        User savedUser = userRepository.save(user);
        Club club = fixture.giveMeBuilder(Club.class)
                .set("user", savedUser)
                .set("clubMembers", null)
                .set("score", Score.from(BigDecimal.ZERO))
                .set("deletedAt", null)
                .sample();
        clubRepository.save(club);
        CreateFixZoneCommand command = new CreateFixZoneCommand(
                savedUser.getId(),
                "test",
                "test",
                List.of("/test/file/2024-01-01/uuid", "/test/file/2024-01-02/uuid")
        );

        //when
        Long createdFixZoneId = facadeCentralFixZoneService.create(command);

        //then
        Optional<FixZone> result = fixZoneRepository.findById(createdFixZoneId);
        assertThat(result).isPresent();
    }

    @DisplayName("동아리 - 내 픽스존 목록 조회")
    @Test
    void getMyFixZones() {
        //given
        User user = fixture.giveMeOne(User.class);
        User savedUser = userRepository.save(user);
        Club club = fixture.giveMeBuilder(Club.class)
                .set("user", savedUser)
                .set("clubMembers", null)
                .set("score", Score.from(BigDecimal.ZERO))
                .set("deletedAt", null)
                .sample();
        Club savedClub = clubRepository.save(club);
        List<FixZone> fixZones = fixture.giveMeBuilder(FixZone.class)
                .set("club", savedClub)
                .set("isCompleted", false)
                .set("deletedAt", null)
                .sampleList(5);
        fixZoneRepository.saveAll(fixZones);

        //when
        List<CentralMyFixZoneListQuery> result = facadeCentralFixZoneService.getMyFixZones(savedUser.getId());

        //then
        assertThat(result).hasSize(5);
    }


    @DisplayName("동아리 - 픽스존 조회")
    @Test
    void getFixZone() {
        //given
        Club club = fixture.giveMeBuilder(Club.class)
                .set("user", null)
                .set("clubMembers", null)
                .set("score", Score.from(BigDecimal.ZERO))
                .set("deletedAt", null)
                .set("profileImageKey", "test/file/2024-01-01/uuid")
                .sample();
        Club savedClub = clubRepository.save(club);
        FixZone fixZone = fixture.giveMeBuilder(FixZone.class)
                .set("club", savedClub)
                .set("isCompleted", false)
                .set("deletedAt", null)
                .set("imageKeys", List.of("test/file/2024-01-01/uuid", "test/file/2024-01-02/uuid"))
                .sample();
        FixZone savedFixZone = fixZoneRepository.save(fixZone);

        //when
        facadeCentralFixZoneService.getFixZone(savedFixZone.getId());

        //then
        Optional<FixZone> result = fixZoneRepository.findById(savedFixZone.getId());
        assertThat(result.isPresent()).isTrue();
    }

    @DisplayName("동아리 - 픽스존 수정")
    @Test
    void update() {
        //given
        FixZone fixZone = fixture.giveMeBuilder(FixZone.class)
                .set("club", null)
                .set("isCompleted", false)
                .set("deletedAt", null)
                .set("imageKeys", List.of("test/file/2024-01-01/uuid", "test/file/2024-01-02/uuid"))
                .sample();
        FixZone savedFixZone = fixZoneRepository.save(fixZone);
        UpdateFixZoneCommand command = new UpdateFixZoneCommand(
                savedFixZone.getId(),
                "test",
                "test",
                List.of("/test/file/2024-01-01/uuid", "/test/file/2024-01-02/uuid")
        );

        //when
        facadeCentralFixZoneService.update(command);

        //then
        FixZone result = fixZoneRepository.findById(savedFixZone.getId()).orElseThrow();
        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getContent()).isEqualTo("test");
    }

    @DisplayName("동아리 - 픽스존 삭제")
    @Test
    void delete() {
        //given
        FixZone fixZone = fixture.giveMeBuilder(FixZone.class)
                .set("club", null)
                .set("isCompleted", false)
                .set("deletedAt", null)
                .set("imageKeys", List.of("test/file/2024-01-01/uuid", "test/file/2024-01-02/uuid"))
                .sample();
        FixZone savedFixZone = fixZoneRepository.save(fixZone);

        //when
        facadeCentralFixZoneService.delete(savedFixZone.getId());

        //then
        Optional<FixZone> result = fixZoneRepository.findById(savedFixZone.getId());
        assertThat(result.isPresent()).isFalse();
    }
}
