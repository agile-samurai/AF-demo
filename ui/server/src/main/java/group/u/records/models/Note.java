package group.u.records.models;

import java.time.LocalDateTime;

public class Note {
    private LocalDateTime timeStamp;
    private String user;
    private String note;

    public Note() {
    }

    public Note(LocalDateTime timeStamp, String user, String note) {
        this.timeStamp = timeStamp;
        this.user = user;
        this.note = note;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getUser() {
        return user;
    }

    public String getNote() {
        return note;
    }
}
