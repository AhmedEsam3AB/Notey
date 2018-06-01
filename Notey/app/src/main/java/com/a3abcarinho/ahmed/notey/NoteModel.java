package com.a3abcarinho.ahmed.notey;

public class NoteModel {

    public String title;
    public String time;

    public NoteModel() {

    }

    public NoteModel(String title, String time) {
        this.title = title;
        this.time = time;
    }
// Title and Time Getting and setting Methods
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}