package com.lap.application.beans;

import java.io.Serializable;

public class TeamDetailStatsBean implements Serializable {
    String heading;
    String value;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
