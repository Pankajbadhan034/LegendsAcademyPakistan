package com.lap.application.beans;

import java.io.Serializable;
import java.util.ArrayList;

public class CoachPlayerDetailStatsBean implements Serializable {
    String league_name;
    ArrayList<SessionLeagueBean> sessionLeagueBeanArrayList;

    public ArrayList<SessionLeagueBean> getSessionLeagueBeanArrayList() {
        return sessionLeagueBeanArrayList;
    }

    public void setSessionLeagueBeanArrayList(ArrayList<SessionLeagueBean> sessionLeagueBeanArrayList) {
        this.sessionLeagueBeanArrayList = sessionLeagueBeanArrayList;
    }

    public String getLeague_name() {
        return league_name;
    }

    public void setLeague_name(String league_name) {
        this.league_name = league_name;
    }


}
