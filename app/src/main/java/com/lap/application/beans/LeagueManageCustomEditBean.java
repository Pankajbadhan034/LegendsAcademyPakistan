package com.lap.application.beans;

import java.io.Serializable;

public class LeagueManageCustomEditBean  implements Serializable {
    String id;
    String name;
    String fee;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
