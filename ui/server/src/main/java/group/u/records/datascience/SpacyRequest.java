package group.u.records.datascience;

public class SpacyRequest {
    private String text;
    private String model = "en";

    public String getText() {
        return text;
    }

    public String getModel() {
        return model;
    }

    public SpacyRequest(){}

    public SpacyRequest(String text) {
        this.text = text;
    }
}
