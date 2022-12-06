package com.lap.application;

import java.io.Serializable;

public class Chapter implements Serializable {
     int id;
     String chapterName;
     String imageUrl;
     String status;
     String termId;
     String attendance_id;
     String midweekSessionDetailsId;
     String orderMidWeekSessionId;
     String newDateFormat;
     boolean boolLocalDelete;
     String unpaidFlag;

    public String getUnpaidFlag() {
        return unpaidFlag;
    }

    public void setUnpaidFlag(String unpaidFlag) {
        this.unpaidFlag = unpaidFlag;
    }

    public boolean isBoolLocalDelete() {
        return boolLocalDelete;
    }

    public void setBoolLocalDelete(boolean boolLocalDelete) {
        this.boolLocalDelete = boolLocalDelete;
    }

    public String getNewDateFormat() {
        return newDateFormat;
    }

    public void setNewDateFormat(String newDateFormat) {
        this.newDateFormat = newDateFormat;
    }

    public String getOrderMidWeekSessionId() {
        return orderMidWeekSessionId;
    }

    public void setOrderMidWeekSessionId(String orderMidWeekSessionId) {
        this.orderMidWeekSessionId = orderMidWeekSessionId;
    }

    String childId;

    public String getMidweekSessionDetailsId() {
        return midweekSessionDetailsId;
    }

    public void setMidweekSessionDetailsId(String midweekSessionDetailsId) {
        this.midweekSessionDetailsId = midweekSessionDetailsId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getAttendance_id() {
        return attendance_id;
    }

    public void setAttendance_id(String attendance_id) {
        this.attendance_id = attendance_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}