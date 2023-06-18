package com.example.efficenz.model;

public class Data {

    private String title;
    private String note;
    private String date;
    private long timestamp;
    private String dueDate;
    private String dueTime; // hh:mma 01:12pm
    private String id;

    public Data(){}

    public Data(String title, String note, String date, long timestamp, String dueDate, String dueTime, String id) {
        this.title = title;
        this.note = note;
        this.date = date;
        this.timestamp = timestamp;
        this.dueDate = dueDate;
        this.dueTime = dueTime;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }
}
