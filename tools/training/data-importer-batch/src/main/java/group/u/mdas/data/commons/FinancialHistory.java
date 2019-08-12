package group.u.mdas.data.commons;

import org.apache.commons.csv.CSVRecord;

public class FinancialHistory {

    public static final int DATE_FIELD = 0;
    public static final int HIGH_FIELD = 4;
    public static final int LOW_FIELD = 5;
    public static final int CLOSE_FIELD = 1;
    public static final int VOLUME_FIELD = 2;
    public static final int OPEN_FIELD = 3;
    private long id;

    private String ticker;
    private String date;
    private double high;
    private double low;
    private double close;
    private double volume;
    private double open;

    public long getId() {
        return id;
    }

    public String getTicker() {
        return ticker;
    }

    public FinancialHistory(){}

    public FinancialHistory(String ticker, String date, double high, double low, double close, double volume, double open) {
        this.ticker = ticker;
        this.date = date;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.open = open;
    }

    public static FinancialHistory from(String ticker, CSVRecord record) {
        return new FinancialHistory(ticker, record.get(DATE_FIELD),
                Double.valueOf(record.get(HIGH_FIELD)),
                Double.valueOf(record.get(LOW_FIELD)),
                Double.valueOf(record.get(CLOSE_FIELD)),
                Double.valueOf(record.get(VOLUME_FIELD)),
                Double.valueOf(record.get(OPEN_FIELD)));
    }

    public String getDate() {
        return date;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public double getVolume() {
        return volume;
    }

    public double getOpen() {
        return open;
    }
}
