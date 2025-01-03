package com.example.moni;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense")
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private double amount;
    private String category;
    private String subcategory;
    private String date;
    private String description;
    private String color;
    private String currency;
    private boolean isRecurring;
    private String recurringPeriod;

    // Constructor without 'type' field
    public Expense(int userId, double amount, String category, String subcategory,
                   String date, String description, String color, String currency,
                   boolean isRecurring, String recurringPeriod) {
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.subcategory = subcategory;
        this.date = date;
        this.description = description;
        this.color = color;
        this.currency = currency;
        this.isRecurring = isRecurring;
        this.recurringPeriod = recurringPeriod;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }

    public String getRecurringPeriod() { return recurringPeriod; }
    public void setRecurringPeriod(String recurringPeriod) { this.recurringPeriod = recurringPeriod; }
}
