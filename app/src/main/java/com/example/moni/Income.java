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
    private String color;  // New field for color
    private String currency;  // New field for currency

    // Constructor including new fields
    public Income(int userId, double amount, String type, String date, String description, String color, String currency) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
        this.color = color;  // Initialize color
        this.currency = currency;  // Initialize currency
    }

    // Getter and Setter for userId
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    // Getter and Setter for color
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    // Getter and Setter for currency
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

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
