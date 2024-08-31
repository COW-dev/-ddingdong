package ddingdong.ddingdongBE.domain.scorehistory.entity;

import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Score {

    @Column(name = "score", nullable = false, columnDefinition = "DECIMAL(10,3) DEFAULT 0")
    private BigDecimal value;

    private Score(BigDecimal value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Score score = (Score) o;
        return getValue() == score.getValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    public static Score from(BigDecimal value) {
        return new Score(value);
    }

}
