package group.u.mdas.models.entity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TextClassificationTestsPostgres {
    private TextClassificationPostgres textClassification;
    private final String text = "messages";
    private final String comparisonText = "mssg";
    private final String score = "0.50";
    private final Long id = 1234L;

    @Before
    public void setUp() {
        textClassification = new TextClassificationPostgres(text, comparisonText, score);
    }

    @Test
    public void setAndGetId() {
        textClassification.setId(id);

        assertEquals(id, textClassification.getId());
    }

    @Test
    public void setAndGetText() {
        textClassification.setText(text);

        assertEquals(text, textClassification.getText());
    }

    @Test
    public void setAndGetComparison_text() {
        textClassification.setComparisonText(comparisonText);

        assertEquals(comparisonText, textClassification.getComparisonText());
    }

    @Test
    public void setAndGetScore() {
        textClassification.setScore(score);

        assertEquals(score, textClassification.getScore());
    }

    @Test
    public void equals_shouldEqual_self() {
        assertEquals(textClassification, textClassification);
    }

    @Test
    public void equals_shouldNotEqual_null() {
        assertNotEquals(textClassification, null);
    }

    @Test
    public void equals_shouldNotEqual_otherObject() {
        assertNotEquals(textClassification, "");
    }

    @Test
    public void equals_shouldEqual_copy() {
        TextClassificationPostgres expected = new TextClassificationPostgres(text, comparisonText, score);
        TextClassificationPostgres actual = new TextClassificationPostgres(text, comparisonText, score);
        expected.setId(id);
        actual.setId(id);

        assertEquals(expected, actual);
    }

    @Test
    public void hashcode_shouldBeEqual() {
        TextClassificationPostgres expected = new TextClassificationPostgres(text, comparisonText, score);
        TextClassificationPostgres actual = new TextClassificationPostgres(text, comparisonText, score);
        expected.setId(id);
        actual.setId(id);

        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    public void hashcode_shouldNotBeEqual() {
        TextClassificationPostgres expected = new TextClassificationPostgres(text, comparisonText, score);
        TextClassificationPostgres actual = new TextClassificationPostgres(text, comparisonText, score);
        expected.setId(id);

        assertNotEquals(expected.hashCode(), actual.hashCode());
    }
}
