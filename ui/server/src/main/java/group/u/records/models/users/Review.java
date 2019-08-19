package group.u.records.models.users;

public class Review {

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    private String rating;
    private String title;
    private String content;

    public Review() {
    }

    public Review(String rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
