package ddingdong.ddingdongBE.domain.filemetadata.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.github.f4b6a3.uuid.UuidCreator;
import com.navercorp.fixturemonkey.FixtureMonkey;
import ddingdong.ddingdongBE.common.support.FixtureMonkeyFactory;
import ddingdong.ddingdongBE.common.support.TestContainerSupport;
import ddingdong.ddingdongBE.domain.filemetadata.entity.DomainType;
import ddingdong.ddingdongBE.domain.filemetadata.entity.FileMetaData;
import ddingdong.ddingdongBE.domain.filemetadata.entity.FileStatus;
import ddingdong.ddingdongBE.domain.filemetadata.repository.FileMetaDataRepository;
import ddingdong.ddingdongBE.domain.filemetadata.service.dto.command.CreateFileMetaDataCommand;
import ddingdong.ddingdongBE.domain.filemetadata.service.dto.command.UpdateAllFileMetaDataCommand;
import ddingdong.ddingdongBE.domain.filemetadata.service.dto.query.FileMetaDataListQuery;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FacadeFileMetaDataServiceImplTest extends TestContainerSupport {

    @Autowired
    private FacadeFileMetaDataService facadeFileMetaDataService;
    @Autowired
    private FileMetaDataRepository fileMetaDataRepository;

    private final FixtureMonkey fixture = FixtureMonkeyFactory.getNotNullBuilderIntrospectorMonkey();

    @DisplayName("FileMetaData 생성")
    @Test
    void create() {
        //given
        UUID id = UuidCreator.getTimeOrderedEpoch();
        CreateFileMetaDataCommand command =
                new CreateFileMetaDataCommand(id, "local/file/2024-01-01/" + id, "test.jpg");

        //when
        UUID createdFileMetaDataId = facadeFileMetaDataService.create(command);

        //then
        Optional<FileMetaData> fileMetaData = fileMetaDataRepository.findById(createdFileMetaDataId);
        assertThat(fileMetaData).isPresent();
    }

    @DisplayName("FileMetaData 조회")
    @Test
    void getAllByEntityTypeAndEntityId() {
        //given
        DomainType domainType = DomainType.CLUB_PROFILE;
        Long entityId = 1L;
        fileMetaDataRepository.saveAll(fixture.giveMeBuilder(FileMetaData.class)
                .set("domainType", domainType)
                .set("entityId", entityId)
                .set("fileStatus", FileStatus.COUPLED)
                .sampleList(3));

        //when
        List<FileMetaDataListQuery> result =
                facadeFileMetaDataService.getAllByEntityTypeAndEntityId(domainType, entityId);

        //then
        assertThat(result).hasSize(3);
    }

    @DisplayName("FileMetaData 수정 - COUPLED")
    @Test
    void updateAllToActivated() {
        //given
        DomainType domainType = DomainType.CLUB_PROFILE;
        Long entityId = 1L;
        UUID id1 = UuidCreator.getTimeOrderedEpoch();
        UUID id2 = UuidCreator.getTimeOrderedEpoch();
        fileMetaDataRepository.saveAll(List.of(
                fixture.giveMeBuilder(FileMetaData.class)
                        .set("id", id1)
                        .set("domainType", null)
                        .set("entityId", null)
                        .set("fileStatus", FileStatus.PENDING)
                        .sample(),
                fixture.giveMeBuilder(FileMetaData.class)
                        .set("id", id2)
                        .set("domainType", null)
                        .set("entityId", null)
                        .set("fileStatus", FileStatus.PENDING)
                        .sample()
        ));

        UpdateAllFileMetaDataCommand command = new UpdateAllFileMetaDataCommand(List.of(id1.toString(), id2.toString()),
                domainType, entityId);
        //when
        facadeFileMetaDataService.updateAll(command);

        //then
        List<FileMetaData> result = fileMetaDataRepository.findAllByDomainTypeAndEntityIdWithFileStatus(
                domainType, entityId, FileStatus.COUPLED);
        assertThat(result).hasSize(2)
                .extracting("fileStatus")
                .contains(FileStatus.COUPLED);
    }

    @DisplayName("FileMetaData 수정 - COUPLED & DELETED")
    @Test
    void updateAllToActivatedAndAttached() {
        //given
        DomainType domainType = DomainType.CLUB_PROFILE;
        Long entityId = 1L;
        UUID id1 = UuidCreator.getTimeOrderedEpoch();
        UUID id2 = UuidCreator.getTimeOrderedEpoch();
        fileMetaDataRepository.saveAll(List.of(
                fixture.giveMeBuilder(FileMetaData.class)
                        .set("id", id1)
                        .set("domainType", null)
                        .set("entityId", null)
                        .set("fileStatus", FileStatus.PENDING)
                        .sample(),
                fixture.giveMeBuilder(FileMetaData.class)
                        .set("id", id2)
                        .set("domainType", domainType)
                        .set("entityId", entityId)
                        .set("fileStatus", FileStatus.COUPLED)
                        .sample()
        ));

        UpdateAllFileMetaDataCommand command =
                new UpdateAllFileMetaDataCommand(List.of(id1.toString()), domainType, entityId);
        //when
        facadeFileMetaDataService.updateAll(command);

        //then
        List<FileMetaData> result = fileMetaDataRepository.findAllByDomainTypeAndEntityIdWithFileStatus(
                domainType, entityId, FileStatus.COUPLED);
        FileMetaData attachedFileMetaData = fileMetaDataRepository.findById(id2).orElseThrow();
        assertThat(result).hasSize(1)
                .extracting("id", "fileStatus")
                .contains(tuple(id1, FileStatus.COUPLED));
        assertThat(attachedFileMetaData.getFileStatus()).isEqualTo(FileStatus.DELETED);
    }

    @DisplayName("FileMetaData 수정 - DELETED")
    @Test
    void updateAllToAttached() {
        //given
        DomainType domainType = DomainType.CLUB_PROFILE;
        Long entityId = 1L;
        UUID id1 = UuidCreator.getTimeOrderedEpoch();
        UUID id2 = UuidCreator.getTimeOrderedEpoch();
        fileMetaDataRepository.saveAll(List.of(
                fixture.giveMeBuilder(FileMetaData.class)
                        .set("id", id1)
                        .set("domainType", domainType)
                        .set("entityId", entityId)
                        .set("fileStatus", FileStatus.COUPLED)
                        .sample(),
                fixture.giveMeBuilder(FileMetaData.class)
                        .set("id", id2)
                        .set("domainType", domainType)
                        .set("entityId", entityId)
                        .set("fileStatus", FileStatus.COUPLED)
                        .sample()
        ));

        UpdateAllFileMetaDataCommand command =
                new UpdateAllFileMetaDataCommand(List.of(), domainType, entityId);
        //when
        facadeFileMetaDataService.updateAll(command);

        //then
        List<FileMetaData> result = fileMetaDataRepository.findByIdIn(List.of(id1, id2));
        assertThat(result).hasSize(2)
                .allSatisfy(fileMetaData -> {
                    assertThat(fileMetaData.getDomainType()).isEqualTo(domainType);
                    assertThat(fileMetaData.getEntityId()).isEqualTo(entityId);
                    assertThat(fileMetaData.getFileStatus()).isEqualTo(FileStatus.DELETED);
                });
    }


}