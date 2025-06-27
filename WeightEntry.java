package com.example.weighttrackerapp;

public class WeightEntry {
    private int id;
    private float weight;
    private String date;

    public WeightEntry(int id, float weight, String date) {
        this.id = id;
        this.weight = weight;
        this.date = date;
    }

    public int getId() {
        return id;
    }
    public float getWeight() {
        return weight;
    }
    public String getDate() {
        return date;
    }
}
