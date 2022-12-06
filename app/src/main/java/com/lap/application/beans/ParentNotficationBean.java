package com.lap.application.beans;

import java.io.Serializable;

public class ParentNotficationBean implements Serializable {
    String title;
    String message;
    String created_at_date;
    String created_at_time;

    public String getCreated_at_date() {
        return created_at_date;
    }

    public void setCreated_at_date(String created_at_date) {
        this.created_at_date = created_at_date;
    }

    public String getCreated_at_time() {
        return created_at_time;
    }

    public void setCreated_at_time(String created_at_time) {
        this.created_at_time = created_at_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
