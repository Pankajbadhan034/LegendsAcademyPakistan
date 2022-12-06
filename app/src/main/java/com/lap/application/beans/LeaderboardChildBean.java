package com.lap.application.beans;

import java.io.Serializable;

public class LeaderboardChildBean implements Serializable{

    private String challengersId;
    private String childId;
    private String childName;
    private String childDpUrl;
    private String scores;
    private String timeTaken;
    private String challengeMedia;
    private String videoThumbnail;

    public String getVideoThumbnail() {
        return videoThumbnail;
    }
    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }



    public String getChallengeMedia() {
        return challengeMedia;
    }
    public void setChallengersMedia(String challengeMedia) {
        this.challengeMedia = challengeMedia;
    }


    public String getChallengersId() {
        return challengersId;
    }

    public void setChallengersId(String challengersId) {
        this.challengersId = challengersId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildDpUrl() {
        return childDpUrl;
    }

    public void setChildDpUrl(String childDpUrl) {
        this.childDpUrl = childDpUrl;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}