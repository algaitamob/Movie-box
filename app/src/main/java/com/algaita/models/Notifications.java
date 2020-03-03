package com.algaita.models;

public class Notifications {
    String id, title, message, ondate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOndate(String ondate) {
        this.ondate = ondate;
    }

    public String getOndate() {
        return ondate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
