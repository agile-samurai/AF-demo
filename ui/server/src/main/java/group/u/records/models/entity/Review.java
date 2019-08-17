package group.u.records.models.entity;

import java.time.LocalDate;

public class Review {

    private String reviewer;
    private LocalDate timeStamp;
    private String content;

    public Review() {
    }

    public Review(String reviewer, LocalDate timeStamp, String content) {
        this.reviewer = reviewer;
        this.timeStamp = timeStamp;
        this.content = content;
    }

    public String getReviewer() {
        return reviewer;
    }

    public LocalDate getTimeStamp() {
        return timeStamp;
    }

    public String getContent() {
        return content;
    }
}
