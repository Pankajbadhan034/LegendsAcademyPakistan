package com.lap.application.beans;

import java.io.Serializable;

public class LeagueTeamDataBean implements Serializable {
    String teamName;
    String selectTeam;
    String selectGroup;
    String checkTeamSelect;
    String fee;
    String groupId;
    String teamId;
    String selectClub;
    String clubId;

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public String getSelectClub() {
        return selectClub;
    }

    public void setSelectClub(String selectClub) {
        this.selectClub = selectClub;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getCheckTeamSelect() {
        return checkTeamSelect;
    }

    public void setCheckTeamSelect(String checkTeamSelect) {
        this.checkTeamSelect = checkTeamSelect;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getSelectTeam() {
        return selectTeam;
    }

    public void setSelectTeam(String selectTeam) {
        this.selectTeam = selectTeam;
    }

    public String getSelectGroup() {
        return selectGroup;
    }

    public void setSelectGroup(String selectGroup) {
        this.selectGroup = selectGroup;
    }
}
