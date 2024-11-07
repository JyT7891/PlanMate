package com.example.planmate.user.ui.task;

public class Task {
    private String title;
    private String date;
    private String time;

    public Task(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
