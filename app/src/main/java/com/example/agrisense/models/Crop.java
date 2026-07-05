package com.example.agrisense.models;

public class Crop {
    private int id;
    private String name;
    private String variety;
    private String plantDate;
    private String harvestDate;
    private String area;
    private String notes;

    public Crop(int id, String name, String variety, String plantDate, String harvestDate, String area, String notes) {
        this.id = id;
        this.name = name;
        this.variety = variety;
        this.plantDate = plantDate;
        this.harvestDate = harvestDate;
        this.area = area;
        this.notes = notes;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getVariety() { return variety; }
    public String getPlantDate() { return plantDate; }
    public String getHarvestDate() { return harvestDate; }
    public String getArea() { return area; }
    public String getNotes() { return notes; }
}