package group.u.mdas.models.web;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TextClassificationResponseTests {
    TextClassificationResponse textClassificationResponse = new TextClassificationResponse();
    private final String text = "messages";
    private final String comparisonText = "mssg";
    private final Double score = 0.50;

    @Before
    public void setUp() {
    }

    @Test
    public void constructor_shouldConstruct() {
        assertNotNull(textClassificationResponse);
    }

    @Test
    public void getText() {
        textClassificationResponse.setText(text);

        String actual = textClassificationResponse.getText();

        assertEquals(text, actual);
    }

    @Test
    public void getComparisonText() {
        textClassificationResponse.setComparisonText(comparisonText);

        String actual = textClassificationResponse.getComparisonText();

        assertEquals(comparisonText, actual);
    }

    @Test
    public void getScore() {
        textClassificationResponse.setScore(score);

        Double actual = textClassificationResponse.getScore();

        assertEquals(score, actual);
    }

    @Test
    public void equals_shouldEqual_self() {
        assertEquals(textClassificationResponse, textClassificationResponse);
    }

    @Test
    public void equals_shouldNotEqual_null() {
        assertNotEquals(textClassificationResponse, null);
    }

    @Test
    public void equals_shouldNotEqual_otherObject() {
        assertNotEquals(textClassificationResponse, "");
    }

    @Test
    public void equals_shouldEqual_copy() {
        TextClassificationResponse expected = new TextClassificationResponse();
        TextClassificationResponse actual = new TextClassificationResponse();
        expected.setScore(score);
        expected.setComparisonText(comparisonText);
        expected.setText(text);

        actual.setScore(score);
        actual.setComparisonText(comparisonText);
        actual.setText(text);

        assertEquals(expected, actual);
    }

    @Test
    public void hashcode_shouldHash() {
        assertNotNull(textClassificationResponse.hashCode());
    }
}
