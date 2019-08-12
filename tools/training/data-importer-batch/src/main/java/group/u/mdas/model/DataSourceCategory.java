package group.u.mdas.model;

public enum DataSourceCategory {
    PRESS_RELEASE("press-releases"),
    SEC_FILING("sec-filing"),
    FINANCIAL_HISTORY("financial-history");

    private String label;

    DataSourceCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
