package com.example.moni;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "offers")
public class Offer {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private long endTime;
    private String imageUrl;
    private boolean isActive;
    private String type; // Add type field

    public Offer(String title, String description, long endTime) {
        this.title = title;
        this.description = description;
        this.endTime = endTime;
        this.isActive = true;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public long getEndTime() { return endTime; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return isActive; }
    public String getType() { return type; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setEndTime(long endTime) { this.endTime = endTime; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setActive(boolean active) { isActive = active; }
    public void setType(String type) { this.type = type; }
}