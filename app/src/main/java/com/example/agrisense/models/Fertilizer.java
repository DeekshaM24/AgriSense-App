package com.example.agrisense.models;

public class Fertilizer {
    private int id;
    private String cropName;
    private String name;
    private String quantity;
    private String date;

    public Fertilizer(int id, String cropName, String name, String quantity, String date) {
        this.id = id;
        this.cropName = cropName;
        this.name = name;
        this.quantity = quantity;
        this.date = date;
    }

    public int getId() { return id; }
    public String getCropName() { return cropName; }
    public String getName() { return name; }
    public String getQuantity() { return quantity; }
    public String getDate() { return date; }
}