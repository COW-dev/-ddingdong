package ddingdong.ddingdongBE.domain.clubpost.entity;

import ddingdong.ddingdongBE.common.BaseEntity;
import ddingdong.ddingdongBE.domain.club.entity.Club;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Table;
import org.hibernate.annotations.Where;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "update club_post set deleted_at = CURRENT_TIMESTAMP where id=?")
@Where(clause = "deleted_at IS NULL")
@Table(appliesTo = "club_post")
public class ClubPost extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private String mediaUrl;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  private Club club;

  @Builder
  private ClubPost(String title, String content, String mediaUrl) {
    this.title = title;
    this.content = content;
    this.mediaUrl = mediaUrl;
  }

  public static ClubPost create(String title, String content, String mediaUrl) {
    return ClubPost.builder()
        .title(title)
        .content(content)
        .mediaUrl(mediaUrl)
        .build();
  }

  public void updateClub(Club club) {
    this.club = club;
  }

  public void update(ClubPost updateClubPost) {
    this.title = updateClubPost.getTitle();
    this.content = updateClubPost.getContent();
    this.mediaUrl = updateClubPost.getMediaUrl();
  }
}
