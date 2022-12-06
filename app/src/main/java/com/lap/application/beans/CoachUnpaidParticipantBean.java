package com.lap.application.beans;

import java.io.Serializable;

public class CoachUnpaidParticipantBean implements Serializable {
    String fullName;
    String parentId;
    String childId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }
}
