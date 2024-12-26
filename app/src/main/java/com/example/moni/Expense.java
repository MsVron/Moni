package com.example.moni;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private double amount;
    private String type;
    private String date;
    private String description;
    private String color;
    private String currency;

    public Expense(int userId, double amount, String type, String date, String description, String color, String currency) {
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.description = description;
        this.color = color;
        this.currency = currency;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}