package group.u.records.models.web;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class TextClassificationResponse {
    private String text;
    private String comparisonText;
    private Double score;

    public TextClassificationResponse() {
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TextClassificationResponse that = (TextClassificationResponse) o;

        return new EqualsBuilder()
                .append(text, that.text)
                .append(comparisonText, that.comparisonText)
                .append(score, that.score)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(text)
                .append(comparisonText)
                .append(score)
                .toHashCode();
    }
}
