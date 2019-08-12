package group.u.mdas.model;


public class TickerDetails {
    private String ticker;
    private String name;
    private String industry;
    private String sector;

    public String getName() {
        return name;
    }

    public String getIndustry() {
        return industry;
    }

    public String getSector() {
        return sector;
    }

    public TickerDetails(){}
    public TickerDetails(CompanyIdentifier companyIdentifier) {
        this.ticker = companyIdentifier.getSymbol();
        this.name = companyIdentifier.getName();
        this.sector = companyIdentifier.getSector();
        this.industry = companyIdentifier.getIndustry();
    }

    public String getTicker() {
        return ticker;
    }
}
