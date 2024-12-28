package com.example.moni;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "premium_features")
public class PremiumFeature {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private boolean isActive;

    public PremiumFeature(String name, String description) {
        this.name = name;
        this.description = description;
        this.isActive = true;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setActive(boolean active) { isActive = active; }
}