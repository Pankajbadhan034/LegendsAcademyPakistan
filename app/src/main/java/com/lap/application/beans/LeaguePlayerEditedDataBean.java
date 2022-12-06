package com.lap.application.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class LeaguePlayerEditedDataBean implements Serializable {
    String id;
    String academy_id;
    String team_id;
    String squad_number;
    String nationality;
    String position;
    String team;
    String league;
    String seasons;
    String height;
    String weight;
    String name;
    String dob;
    String description;
    String image;
    String state;
    String created_by;
    String school;
    String parentContactNumber;
    String parentContactEmail;
    ArrayList<LeaguePlayerChooseDocBean>leaguePlayerChooseDocBeanArrayList = new ArrayList<>();
    String club;

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public ArrayList<LeaguePlayerChooseDocBean> getLeaguePlayerChooseDocBeanArrayList() {
        return leaguePlayerChooseDocBeanArrayList;
    }

    public void setLeaguePlayerChooseDocBeanArrayList(ArrayList<LeaguePlayerChooseDocBean> leaguePlayerChooseDocBeanArrayList) {
        this.leaguePlayerChooseDocBeanArrayList = leaguePlayerChooseDocBeanArrayList;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getParentContactNumber() {
        return parentContactNumber;
    }

    public void setParentContactNumber(String parentContactNumber) {
        this.parentContactNumber = parentContactNumber;
    }

    public String getParentContactEmail() {
        return parentContactEmail;
    }

    public void setParentContactEmail(String parentContactEmail) {
        this.parentContactEmail = parentContactEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcademy_id() {
        return academy_id;
    }

    public void setAcademy_id(String academy_id) {
        this.academy_id = academy_id;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getSquad_number() {
        return squad_number;
    }

    public void setSquad_number(String squad_number) {
        this.squad_number = squad_number;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getSeasons() {
        return seasons;
    }

    public void setSeasons(String seasons) {
        this.seasons = seasons;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }
}
