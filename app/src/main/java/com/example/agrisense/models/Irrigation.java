package com.example.agrisense.models;

public class Irrigation {
    private int id;
    private String cropName;
    private String date;
    private String status;

    public Irrigation(int id, String cropName, String date, String status) {
        this.id = id;
        this.cropName = cropName;
        this.date = date;
        this.status = status;
    }

    public int getId() { return id; }
    public String getCropName() { return cropName; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}