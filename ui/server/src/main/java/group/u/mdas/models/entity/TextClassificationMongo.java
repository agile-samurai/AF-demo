package group.u.mdas.models.entity;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TextClassificationMongo {
    @Id
    private String id;

    @Indexed
    private String text;

    private String comparisonText;

    private String score;

    public TextClassificationMongo(String text, String comparisonText, String score) {
        this.text = text;
        this.comparisonText = comparisonText;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

        TextClassificationMongo that = (TextClassificationMongo) o;

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
