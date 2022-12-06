package com.lap.application.beans;

import android.net.Uri;

import java.io.Serializable;

public class LeaguePlayerChooseDocBean implements Serializable {
    String title;
    Uri file;
    String fileName;
    String selectedImagePath;
    String id;
    String academy_id;
    String player_id;
    String state;
    String file_url;
    boolean editBoolTitle;
    boolean editBoolDoc;

    public boolean isEditBoolTitle() {
        return editBoolTitle;
    }

    public void setEditBoolTitle(boolean editBoolTitle) {
        this.editBoolTitle = editBoolTitle;
    }

    public boolean isEditBoolDoc() {
        return editBoolDoc;
    }

    public void setEditBoolDoc(boolean editBoolDoc) {
        this.editBoolDoc = editBoolDoc;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
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

    public String getPlayer_id() {
        return player_id;
    }

    public void setPlayer_id(String player_id) {
        this.player_id = player_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSelectedImagePath() {
        return selectedImagePath;
    }

    public void setSelectedImagePath(String selectedImagePath) {
        this.selectedImagePath = selectedImagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }
}
