package group.u.mdas.model;

public class CompanyIdentifier {

    String symbol;

    String name;
    double LastSale;
    double MarketCap;
    double ADR_TSO;
    int IPOyear;
    String sector;
    String industry;
    String summary;
    String quote;

    public CompanyIdentifier() {
    }

    public CompanyIdentifier(String symbol, String name, String sector, String industry) {
        this.symbol = symbol;
        this.name = name;
        this.sector = sector;
        this.industry = industry;
    }

    @Override
    public String toString() {
        return "CompanyIdentifier{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", LastSale=" + LastSale +
                ", MarketCap=" + MarketCap +
                ", ADR_TSO=" + ADR_TSO +
                ", IPOyear=" + IPOyear +
                ", sector='" + sector + '\'' +
                ", industry='" + industry + '\'' +
                ", summary='" + summary + '\'' +
                ", quote='" + quote + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public double getLastSale() {
        return LastSale;
    }

    public double getMarketCap() {
        return MarketCap;
    }

    public double getADR_TSO() {
        return ADR_TSO;
    }

    public int getIPOyear() {
        return IPOyear;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSector() {
        return sector;
    }

    public String getIndustry() {
        return industry;
    }

    public String getSummary() {
        return summary;
    }

    public String getQuote() {
        return quote;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
