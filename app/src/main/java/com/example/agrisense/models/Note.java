package com.example.agrisense.models;

public class Note {
    private int id;
    private String title;
    private String description;
    private String date;

    public Note(int id, String title, String description, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
}