package group.u.mdas.models.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;

@Entity
@Table(name = "text_classification")
public class TextClassificationPostgres {
    public TextClassificationPostgres(String text, String comparisonText, String score) {
        this.text = text;
        this.comparisonText = comparisonText;
        this.score = score;
    }

    @Id
    @Column
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String text;
    private String comparisonText;
    private String score;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getComparisonText() {
        return comparisonText;
    }

    public void setComparisonText(String comparisonText) {
        this.comparisonText = comparisonText;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TextClassificationPostgres that = (TextClassificationPostgres) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(text, that.text)
                .append(comparisonText, that.comparisonText)
                .append(score, that.score)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(text)
                .append(comparisonText)
                .append(score)
                .toHashCode();
    }
}

