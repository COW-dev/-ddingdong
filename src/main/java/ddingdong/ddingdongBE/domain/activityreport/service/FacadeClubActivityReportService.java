package ddingdong.ddingdongBE.domain.activityreport.service;

import static ddingdong.ddingdongBE.domain.fileinformation.entity.FileDomainCategory.ACTIVITY_REPORT;
import static ddingdong.ddingdongBE.domain.fileinformation.entity.FileTypeCategory.IMAGE;
import static ddingdong.ddingdongBE.domain.filemetadata.entity.FileCategory.ACTIVITY_REPORT_IMAGE;

import ddingdong.ddingdongBE.domain.activityreport.domain.ActivityReport;
import ddingdong.ddingdongBE.domain.activityreport.domain.ActivityReportTermInfo;
import ddingdong.ddingdongBE.domain.activityreport.service.dto.command.CreateActivityReportCommand;
import ddingdong.ddingdongBE.domain.activityreport.service.dto.command.UpdateActivityReportCommand;
import ddingdong.ddingdongBE.domain.activityreport.service.dto.query.ActivityReportInfo;
import ddingdong.ddingdongBE.domain.activityreport.service.dto.query.ActivityReportListQuery;
import ddingdong.ddingdongBE.domain.activityreport.service.dto.query.ActivityReportQuery;
import ddingdong.ddingdongBE.domain.activityreport.service.dto.query.ActivityReportTermInfoQuery;
import ddingdong.ddingdongBE.domain.club.entity.Club;
import ddingdong.ddingdongBE.domain.club.service.ClubService;
import ddingdong.ddingdongBE.domain.fileinformation.service.FileInformationService;
import ddingdong.ddingdongBE.domain.filemetadata.entity.FileMetaData;
import ddingdong.ddingdongBE.domain.filemetadata.service.FileMetaDataService;
import ddingdong.ddingdongBE.domain.user.entity.User;
import ddingdong.ddingdongBE.file.service.FileService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FacadeClubActivityReportService {

    private final ActivityReportService activityReportService;
    private final ActivityReportTermInfoService activityReportTermInfoService;
    private final ClubService clubService;
    private final FileInformationService fileInformationService;
    private final FileService fileService;
    private final FileMetaDataService fileMetaDataService;

    public List<ActivityReportQuery> getActivityReport(
        String term,
        String clubName
    ) {
        List<ActivityReport> activityReports = activityReportService.getActivityReport(
            clubName,
            term);
        return activityReports.stream()
            .map(this::parseToQuery)
            .toList();
    }

    public List<ActivityReportListQuery> getMyActivityReports(User user) {
        Club club = clubService.getByUserId(user.getId());
        List<ActivityReport> activityReports = activityReportService.getActivityReportsByClub(club);
        return parseToListQuery(activityReports);
    }

    public List<ActivityReportTermInfoQuery> getActivityReportTermInfos() {
        List<ActivityReportTermInfo> termInfos = activityReportTermInfoService.getActivityReportTermInfos();
        return termInfos.stream()
            .map(ActivityReportTermInfoQuery::from)
            .toList();
    }

    public String getCurrentTerm() {
        return activityReportTermInfoService.getCurrentTerm();
    }

    @Transactional
    public void create(
        User user,
        List<CreateActivityReportCommand> commands
    ) {
        Club club = clubService.getByUserId(user.getId());
        String term = getRequestTerm(commands);
        commands.forEach(command -> {
            ActivityReport activityReport = command.toEntity(club);
            activityReportService.create(activityReport);
            createFileMetaData(command.key());
        });
    }

    @Transactional
    public void update(
        User user,
        String term,
        List<UpdateActivityReportCommand> commands
    ) {
        Club club = clubService.getByUserId(user.getId());

        List<ActivityReport> activityReports = activityReportService.getActivityReportOrThrow(club.getName(), term);
        List<ActivityReport> updateActivityReports = commands.stream()
            .map(UpdateActivityReportCommand::toEntity)
            .toList();

        activityReportService.update(activityReports, updateActivityReports);

        createFileMetaDatas(updateActivityReports);
    }

    @Transactional
    public void delete(User user, String term) {
        Club club = clubService.getByUserId(user.getId());
        List<ActivityReport> activityReports = activityReportService.getActivityReportOrThrow(
            club.getName(),
            term);
        activityReportService.deleteAll(activityReports);
    }

    private void createFileMetaDatas(List<ActivityReport> activityReports) {
        activityReports.forEach(activityReport -> {
            createFileMetaData(activityReport.getKey());
        });
    }

    private void createFileMetaData(String key) {
        fileMetaDataService.createOne(FileMetaData.of(key, ACTIVITY_REPORT_IMAGE));
    }

    private String getRequestTerm(List<CreateActivityReportCommand> commands) {
        return commands.stream()
            .findFirst()
            .map(CreateActivityReportCommand::term)
            .orElse(null);
    }

    private ActivityReportQuery parseToQuery(ActivityReport activityReport) {
        String imagePath =
            IMAGE.getFileType() + ACTIVITY_REPORT.getFileDomain() + activityReport.getId();
        List<String> imageUrls = fileInformationService.getImageUrls(imagePath);
        return ActivityReportQuery.of(activityReport, imageUrls);
    }

    private List<ActivityReportListQuery> parseToListQuery(final List<ActivityReport> activityReports) {
        Map<String, Map<String, List<Long>>> groupedData = activityReports.stream().collect(
            Collectors.groupingBy(activityReport -> activityReport.getClub().getName(),
                Collectors.groupingBy(ActivityReport::getTerm,
                    Collectors.mapping(ActivityReport::getId, Collectors.toList()))));

        return groupedData.entrySet().stream().flatMap(entry -> {
            String clubName = entry.getKey();
            Map<String, List<Long>> termMap = entry.getValue();

            return termMap.entrySet().stream().map(termEntry -> {
                String term = termEntry.getKey();
                List<ActivityReportInfo> activityReportInfos = termEntry.getValue().stream()
                    .map(ActivityReportInfo::new)
                    .toList();
                return ActivityReportListQuery.of(clubName, term, activityReportInfos);
            });

        }).toList();
    }
}
