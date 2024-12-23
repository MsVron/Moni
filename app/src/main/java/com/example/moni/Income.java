package com.example.moni;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "income")
public class Income {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;  // Add this field
    private double amount;
    private String type;
    private String date;
    private String description;

    public Income(int userId, double amount, String type, String date, String description) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
    }

    // Add getter and setter for userId
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    // Existing getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
