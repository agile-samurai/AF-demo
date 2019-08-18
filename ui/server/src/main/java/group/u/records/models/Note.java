package group.u.records.models;

public class Note {
    private String timeStamp;
    private String user;
    private String note;

    public Note() {
    }

    public Note(String timeStamp, String user, String note) {
        this.timeStamp = timeStamp;
        this.user = user;
        this.note = note;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getUser() {
        return user;
    }

    public String getNote() {
        return note;
    }
}
