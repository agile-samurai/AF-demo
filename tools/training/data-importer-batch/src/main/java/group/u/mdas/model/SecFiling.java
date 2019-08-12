package group.u.mdas.model;

public class SecFiling {
    private String business;
    private String risk;

    public String getBusiness() {
        return business;
    }

    public String getRisk() {
        return risk;
    }

    public SecFiling(String business, String risk) {
        this.business = business;
        this.risk = risk;
    }

    @Override
    public String toString(){
        return business + "\n" + risk;
    }
}
