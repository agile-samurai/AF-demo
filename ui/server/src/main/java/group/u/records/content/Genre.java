package group.u.records.content;

public class Genre {
    private final String genre;
    private final String img;

    public String getGenre() {
        return genre;
    }

    public String getImg() {
        return img;
    }

    public Genre(String genre, String img) {
        this.genre = genre;
        this.img = img;
    }
}
